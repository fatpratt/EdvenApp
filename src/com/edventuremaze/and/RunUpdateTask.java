package com.edventuremaze.and;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.edventuremaze.and.maze.PlatformAnd;
import com.edventuremaze.and.maze.SoundEffectsAnd;
import com.edventuremaze.and.utils.FileUtilsAnd;
import com.edventuremaze.maze.Platform;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;



/**
 * This is a an AsyncTask that allows the maze download and update process to occur in the
 * background while the use is updated with progress throughout the process.
 */
public class RunUpdateTask extends AsyncTask<Void, String, String> {
    final static String sLogLabel = "--->RunUpdateTask:";

    final String SERVLET_UPDATE_CHECKER = "UpdateChecker.jsp";
    final String SERVLET_GET_FILE_LIST = "GetFileList.jsp";

    final String RET_UP_TO_DATE = "_UpToDate_";
    final String RET_FOLDER_NAME_NOT_SPECIFIED = "_FolderNameNotSpecified_";
    final String RET_FOLDER_NO_SUCH_FOLDER_FOUND = "_NoSuchFolderFound_";

    final String PARAM_FOLDER_NAME = "folder_name";
    final String PARAM_MAX_FILE_LENGTH = "max_file_length";
    final String PARAM_CLIENT_TIME = "client_time";
    final String PARAM_CLIENT_FILE_TIME = "client_file_time";
    final String PARAM_EXTENSION_IGNORE = "extension_ignore";

    final String MAX_FILE_LENGTH = "200000";

    ProgressDialog fProgDialog;
    Context fContext;
    String fMazeId;
    Activity fCurActivity;
    FileUtilsAnd fFileUtilsAnd;  // android specific FileUtils object used throughout to access file system
    boolean fX2;                // everything is 2 times bigger

    public RunUpdateTask(Context aContext, Activity aCurActivity, String aMazeId, boolean aX2) {
        fContext = aContext;
        fCurActivity = aCurActivity;
        fMazeId = aMazeId;
        fX2 = aX2;

        // need to establish a FileUtils object for use through out this class in accessing the file system
        Platform platform = (Platform)new PlatformAnd(aContext, aCurActivity, fX2);
        fFileUtilsAnd = new FileUtilsAnd(platform);
    }

    /**
     * Called before the thread begins, this method creates and shows the progress dialog.
     */
    @Override
    protected void onPreExecute() {
        fProgDialog = new ProgressDialog(fContext);
        fProgDialog.setIndeterminate(true);
        fProgDialog.setCancelable(true);
        fProgDialog.setMessage("Running maze update...");
        fProgDialog.show();
        super.onPreExecute();
    }

    /**
     * Workhorse method which runs in background mode and actually does the maze update.
     * @param unused (Notice this param type must match the first param in class definition and the
     * return type matches the third param.)
     * @return The return value becomes the param to onPostExecute().  Returns error message, if there
     * is an error encountered, but otherwise returns null.
     */
    @Override
    protected String doInBackground(Void... unused) {
        String retStatus = doInitialDownloads();
        if (retStatus != null) return retStatus;

        retStatus = doCheckForUpdates();
        if (retStatus != null) return retStatus;

        publishProgress("All done");

        // update timestamp on each file so we aren't forced into updating them again next time
        fFileUtilsAnd.touchAllFiles(fMazeId);

        return null;
    }

    /**
     * Performs an http request to query the server for all files associated with the selected mazeid.  A list of
     * file names is returned which is compared to the files present on the local file system.  Files missing from
     * the file system are downloaded.  The user is shown progress of the download process.
     * @return Returns a string if problems were encountered, otherwise returns null.
     */
    protected String doInitialDownloads() {
        String host = fContext.getString(R.string.maze_host_url);
        String fullFolder = FileUtilsAnd.appendSlash(fContext.getString(fX2 ? R.string.maze_dir_x2 : R.string.maze_dir)) + fMazeId;
        String urlStr = host + SERVLET_GET_FILE_LIST + "?"
                + PARAM_FOLDER_NAME + "=" + fullFolder + "&"
                + PARAM_EXTENSION_IGNORE + "=" + SoundEffectsAnd.sIgnoreExtension + "&"
                + PARAM_MAX_FILE_LENGTH + "=" + MAX_FILE_LENGTH;

        String response = "";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(urlStr);
        try {
            // check for initial file downloads (first time downloaded)
            publishProgress("Checking initial downloads");
            HttpResponse execute = client.execute(httpGet);

            // check the http return status code
            int statusCode = execute.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                publishProgress("Unable to connect with server");
                Log.d(sLogLabel, "Unable to connect with server:" + urlStr);
                return "Unable to connect with server";
            }

            // get response from GetFileList url
            InputStream content = execute.getEntity().getContent();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String line = "";
            while ((line = buffer.readLine()) != null) {
                response += line;
            }
            Log.d(sLogLabel, SERVLET_GET_FILE_LIST + " request: "  + urlStr);
            Log.d(sLogLabel, SERVLET_GET_FILE_LIST + " response: "  + response);

            // check response for return values and errors
            if (response.equals(RET_FOLDER_NAME_NOT_SPECIFIED) || response.equals(RET_FOLDER_NO_SUCH_FOLDER_FOUND)) {
                publishProgress("No such maze found (id " + fMazeId + ")");
                Log.d(sLogLabel, "Error--No such maze found (id " + fMazeId + ")");
                return "No such maze found (id " + fMazeId + ")";
            }

            // response from GetFileList url contains file names... look at each file
            String delims = "[:]";
            String[] tokens = response.split(delims);

            // for each file check to see if it exist... if not, download it
            for (String fileName: tokens) {
                if (!fFileUtilsAnd.doesFileExistOnDevice(fMazeId, fileName)) {
                    publishProgress("Downloading " + fileName);
                    fFileUtilsAnd.download(fMazeId, fileName, fX2);
                    //if (!fileSys.doesFileExistOnDevice(fMazeId, fileName))
                    //Log.d(sLogLabel, "Error--File not found after downloading: " + fileName);
                }
            }

        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.d(sLogLabel, "Error: " + e);
            return("Error: " + e);
        }
        return null;
    }

    /**
     * Performs an http request to query the server for file updates associated with the selected mazeid.  A list
     * of files requiring updates is returned from the server and such files are downloaded. The user is shown
     * progress of the update process.  Which files should be downloaded in the update process is determined
     * by file times on the client compared with file times on the server.  If the modified file date on the server
     * is younger than the modified file date on the device, the file is flagged for downloading. /
     * @return Returns a string if problems were encountered, otherwise returns null.
     */
    protected String doCheckForUpdates() {
        Long oldestFileTime = fFileUtilsAnd.getOldestFileTime(fMazeId);
        Date curDate = new Date();
        Long curTime = curDate.getTime();   // current time in millis since 1/1/70 GMT

        // The server expects the request to contain the current system time on the device, and the time of
        // the oldest file on the device (associated with the maze).  This is used to determine the age of
        // the files and compare them with age of the corresponding files on the server to see if an update is
        // needed.  For the sake of simplicity the oldest file date represents the modified file date for all
        // files rather than uploading a complete list of files and modified dates.

        String host = fContext.getString(R.string.maze_host_url);
        String fullFolder = FileUtilsAnd.appendSlash(fContext.getString(fX2 ? R.string.maze_dir_x2 : R.string.maze_dir)) + fMazeId;
        String urlStr = host + SERVLET_UPDATE_CHECKER + "?"
                + PARAM_FOLDER_NAME + "=" + fullFolder + "&"
                + PARAM_CLIENT_TIME + "=" + curTime + "&"
                + PARAM_CLIENT_FILE_TIME + "=" + oldestFileTime + "&"
                + PARAM_EXTENSION_IGNORE + "=" + SoundEffectsAnd.sIgnoreExtension + "&"
                + PARAM_MAX_FILE_LENGTH + "=" + MAX_FILE_LENGTH;

        String response = "";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(urlStr);
        try {
            // check for file updates
            publishProgress("Checking for updates");
            HttpResponse execute = client.execute(httpGet);

            // check the http return status code
            int statusCode = execute.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                publishProgress("Unable to connect with server");
                Log.d(sLogLabel, "Unable to connect with server:" + urlStr);
                return "Unable to connect with server";
            }

            // get response from UpdateChecker url
            InputStream content = execute.getEntity().getContent();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String line = "";
            while ((line = buffer.readLine()) != null) {
                response += line;
            }
            Log.d(sLogLabel, SERVLET_UPDATE_CHECKER + " request: "  + urlStr);
            Log.d(sLogLabel, SERVLET_UPDATE_CHECKER + " response: "  + response);

            // check response for return values and errors
            if (response.equals(RET_FOLDER_NAME_NOT_SPECIFIED) || response.equals(RET_FOLDER_NO_SUCH_FOLDER_FOUND)) {
                publishProgress("No such maze found (id " + fMazeId + ")");
                Log.d(sLogLabel, "Error--No such maze found (id " + fMazeId + ")");
                return "No such maze found (id " + fMazeId + ")";
            }
            if (response.equals(RET_UP_TO_DATE)) {
                publishProgress("Up to date");
                return null;
            }

            // response from UpdateChecker url contains file names... look at each file
            String delims = "[:]";
            String[] tokens = response.split(delims);

            // download each file whether it exits or not
            for (String fileName: tokens) {
                publishProgress("Updating " + fileName);
                fFileUtilsAnd.download(fMazeId, fileName, fX2);
                //if (!fileSys.doesFileExistOnDevice(fMazeId, fileName))
                //Log.d(sLogLabel, "Error--File not found after downloading: " + fileName);
            }
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.d(sLogLabel, "Error: " + e);
            return "Error: " + e;
        }
        return null;
    }

    /**
     * This method is called from the publish progress call and updates
     * the progress dialog. (Notice that the datatype of the second class param gets
     * passed to this method.)
     * @param progress
     */
    @Override
    protected void onProgressUpdate(String... progress) {
        fProgDialog.setMessage(progress[0]);
    }

    /**
     * Called as soon as doInBackground method completes.
     * (Notice that the third class param gets passed to this method.)
     * @param result Can be null if there is no error message returned from the background process.
     */
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            fProgDialog.setMessage(result);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.d(sLogLabel, "Insomnia");
            }
        }
        fProgDialog.dismiss();
        allDone(result == null);
    }


    private void allDone(boolean success) {
        if (success) {
            //Intent intent = getIntent();
            //intent.putExtra("returnedData", fMazeId);
            fCurActivity.finish();
        }
    }

}
