package br.com.tiradividas.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TOKEN_NOTFI = "TOKEN_APP";
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    private Context context;
    private String msgResult;
    private String refreshedToken;

    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID token.
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.i("TOKEN" , refreshedToken);

        if (context != null && refreshedToken != null) {
            LibraryClass.saveSP(context, TOKEN_NOTFI, refreshedToken);
        }
        // Send registration to app's server.
        //registerToken(refreshedToken);
    }

    public String getRefreshedToken() {
        return refreshedToken;
    }

/*
    public void registerToken(String action, String token, String chat_id, String id_user, String message) {

        HttpURLConnection cnn = null;
        String line = "";
        URL url;
        BufferedReader bufferedReader = null;


        String body = "action="+action+"&tokenAPP="+token+"&id_user="+id_user+"&chat_id="+chat_id+"&mensagem="+message;

        byte[] bytes = body.getBytes();

        try {
            url = new URL("http://tiraduvidas.pe.hu/index.php/");

            //Set up connection
            cnn = (HttpURLConnection) url.openConnection();
            cnn.setDoOutput(true);
            cnn.setUseCaches(false);
            cnn.setFixedLengthStreamingMode(bytes.length);
            cnn.setRequestMethod("POST");
            cnn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Post the request
            OutputStream outputStream = cnn.getOutputStream();
            outputStream.write(bytes);
            outputStream.close();

            //Handle the response
            int status = cnn.getResponseCode();
            if (status != 200) {
                Log.i("RES", String.valueOf(status));
                throw new IOException("Post fail with error code:" + status);
            }
            else {
                Log.i("RES", String.valueOf(status));
            }
            bufferedReader = new BufferedReader(new InputStreamReader(cnn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + '\n');
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    public void setContext(Context context) {
        this.context = context;
    }

    public void enviaInfo(String action, String token, String chat_id, String id_user, String message){
        Map<String, String> paramPost = new HashMap<String, String>();
        paramPost.put("action",action);
        paramPost.put("tokenAPP", token);
        paramPost.put("id_user", id_user);
        paramPost.put("chat_id", chat_id);
        paramPost.put("mensagem", message);

        try {
            new EnviaDados().execute(paramPost);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getStringResultFromService_POST(String serviceURL, Map<String, String> params) {
        HttpURLConnection cnn = null;
        String line = null;
        URL url;
        try{
            url = new URL(serviceURL);
        } catch (MalformedURLException e){
            throw  new IllegalArgumentException("URL invalid:"+serviceURL);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        //Construct the post body using the parameter
        while (iterator.hasNext()){
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            if(iterator.hasNext()){
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString(); //format same to arg1=val1&arg2=val2
        Log.w("AccessService", "param:" + body);
        byte[]bytes = body.getBytes();
        try{
            cnn = (HttpURLConnection)url.openConnection();
            cnn.setDoOutput(true);
            cnn.setUseCaches(false);
            cnn.setFixedLengthStreamingMode(bytes.length);
            cnn.setRequestMethod("POST");
            cnn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            //Post the request
            OutputStream outputStream = cnn.getOutputStream();
            outputStream.write(bytes);
            outputStream.close();

            //Handle the response
            int status = cnn.getResponseCode();
            if(status!=200){
                throw  new IOException("Post fail with error code:" + status);
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(cnn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line+'\n');
            }
            return stringBuilder.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public class EnviaDados extends AsyncTask<Map<String, String>,String,String>{

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            return getStringResultFromService_POST("http://tiraduvidas.pe.hu/index.php", maps[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            msgResult = s;
            if (s != null) {
                Log.w("ServiceResponseMsg", s);
            }
        }
    }
}
