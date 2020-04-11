package util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.vehiclepooling.Choice_Activity;
import com.example.vehiclepooling.CustomAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;


public class Connection {
    private static Connection con=new Connection(); //common connection object
    final StitchAppClient client =
            Stitch.initializeDefaultAppClient("pixaride-xmgge");
    String databaseName="PixARide";
    final RemoteMongoClient mongoClient =
            client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

    RemoteMongoCollection<Document> coll;

    boolean isSuccess=false;//used to check wether connection is successful or not
    Task<StitchUser> user;

    public boolean checkConnection(){
        return isSuccess;
    }

    private Connection() {
        user=client.getAuth().loginWithCredential(new AnonymousCredential());
        user.continueWithTask(
                (Continuation<StitchUser, Task<List<Document>>>) task -> {
                    //Log.e("Ada","Called");
                    if (!task.isSuccessful()) {
                      //  Log.e("STITCH", "Login failed!"+task.getException().toString());
                        task.getException().printStackTrace();
                        isSuccess=false;
                        throw task.getException();
                    }
                    isSuccess=true;
                    Log.e("STITCH", "Login success!0"+isSuccess);
                    return null;
                }
        );
    }

    public static Connection getConnection(){
        if(con == null) {
            con=new Connection();
        }
        return con;
    }

    public void insertData(Document doc,String CollectionName, OnInserted onInserted){
        coll=mongoClient.getDatabase(databaseName).getCollection(CollectionName);

        coll.insertOne(doc).addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                onInserted.onInserted(task.isSuccessful());
            }
        });
    }
    public void findData(Document doc,String CollectionName,OnRetrival onRetrival){
        coll=mongoClient.getDatabase(databaseName).getCollection(CollectionName);
        List<Document> docs = new ArrayList<>();

        coll.find(doc).into(docs).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("STITCH", "Found docs: " + task.getResult().toString());
                onRetrival.onRetrival(task.getResult());
                return;
            }
            Log.e("STITCH", "Error: " + task.getException().toString());
            task.getException().printStackTrace();
        });
    }
    public void findProjectedData(Document doc,Document project,String CollectionName,OnRetrival onRetrival){
        coll=mongoClient.getDatabase(databaseName).getCollection(CollectionName);
        List<Document> docs = new ArrayList<>();
        /*RemoteFindIterable findResults = coll
                .find(doc)
                .projection(project);*/
        coll.find(doc).projection(project).into(docs).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("STITCH", "Found docs: " + task.getResult().toString());
                onRetrival.onRetrival(task.getResult());
                return;
            }
            Log.e("STITCH", "Error: " + task.getException().toString());
            task.getException().printStackTrace();
        });
    }
    public void updateData(Document filterDoc,Document updateDoc,String CollectionName,OnUpdated onupdated){
        coll=mongoClient.getDatabase(databaseName).getCollection(CollectionName);
        coll.updateOne(filterDoc,updateDoc).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long numMatched = task.getResult().getMatchedCount();
                long numModified = task.getResult().getModifiedCount();
                Log.d("app", String.format("successfully matched %d and modified %d documents",
                        numMatched, numModified));
            } else {
                Log.e("app", "failed to update document with: ", task.getException());
            }
            onupdated.onUpdated(task.isSuccessful());
        });

    }

}
        /*
            public Task<List<Document>> then(@NonNull Task<RemoteUpdateResult> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e("STITCH", "Update failed!");
                    throw task.getException();
                }
                List<Document> docs = new ArrayList<>();
                return coll
                        .find(new Document("owner_id", client.getAuth().getUser().getId()))
                        .limit(100)
                        .into(docs);
            }
        }).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    Log.d("STITCH", "Found docs: " + task.getResult().toString());
                    return;
                }
                Log.e("STITCH", "Error: " + task.getException().toString());
                task.getException().printStackTrace();
            }
        });*/
        /*
        public class Connection {
            private static Connection con=new Connection(); //common connection object
            boolean isTaskSuccess=false; //used to check queryFiredCorrectly or not
            final StitchAppClient client =
                    Stitch.initializeDefaultAppClient("pixaride-xmgge");

            final RemoteMongoClient mongoClient =
                    client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

            RemoteMongoCollection<Document> coll;

            boolean isSuccess=false;//used to check wether connection is successful or not
            Task<StitchUser> user=null;

            public boolean checkConnection(){
                return isSuccess;
            }

            boolean setMongoCollection(String CollectionName){

                coll=mongoClient.getDatabase("PixARide").getCollection(CollectionName);
                return coll!=null;
            }
            private Connection() {
                user=client.getAuth().loginWithCredential(new AnonymousCredential());
                Log.e("Ada","Constructor Called");
                user.continueWithTask(
                        (Continuation<StitchUser, Task<List<Document>>>) task -> {
                            Log.e("Ada","Called");
                            if (!task.isSuccessful()) {
                                Log.e("STITCH", "Login failed!"+task.getException().toString());
                                task.getException().printStackTrace();
                                isSuccess=false;
                                throw task.getException();
                            }
                            isSuccess=true;
                            Log.e("STITCH", "Login success!0"+isSuccess);
                            return null;
                        }
                );
                Log.e("STITCH", "Login success!1"+isSuccess);
            }

            public static Connection getConnection(){
                if(con == null) {
                    con=new Connection();
                }
                Log.e("STITCH", "Login success!2"+con.isSuccess);
                return con;
            }
            ProgressDialog pd;
            public void insertData(Document doc, Context c,ProgressDialog pd){
                isTaskSuccess=false;
                this.pd=pd;
                coll.insertOne(doc).addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
                    @Override
                    public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                        disp(task.isSuccessful(),c);
                    }
                });
            }
            public void findData(Document doc,Activity c,ProgressDialog pd){
                this.pd=pd;
                List<Document> docs = new ArrayList<>();
                // Toast.makeText(c, "Find ", Toast.LENGTH_SHORT).show();
                coll.find(doc).limit(1).into(docs).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Document>> task) {

                        if (task.isSuccessful()) {
                            Log.d("STITCH", "Found docs: " + task.getResult().toString());
                            changePage(task.getResult().toArray().length>0,c,doc);
                            return;
                        }
                        Log.e("STITCH", "Error: " + task.getException().toString());
                        task.getException().printStackTrace();
                    }
                });
            }

            public void getRegisteredCars(Document doc,Activity c,ListView ls){
                List<Document> docs = new ArrayList<>();
                // Toast.makeText(c, "Find ", Toast.LENGTH_SHORT).show();
                coll.find(doc).into(docs).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Document>> task) {

                        if (task.isSuccessful()) {
                            setData(task.getResult(),c,ls);
                            //changePage(task.getResult().toArray().length>0,c,doc);
                            return;
                        }
                        Log.e("STITCH", "Error: " + task.getException().toString());
                        task.getException().printStackTrace();
                    }
                });
            }

            private void setData(List<Document> result, Activity c, ListView ls) {
                CustomAdapter cs=new CustomAdapter(result,c);
                ls.setAdapter(cs);
            }

            public void disp(Boolean b,Context c){
                //Toast.makeText(c, "Ans"+b, Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
            public void changePage(Boolean b, Activity c, Document doc){
                if(b){

                    SharedPreferences pref = c.getSharedPreferences("LoginDetails", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Email_ID",doc.getString("email"));
                    editor.commit();

                    Intent i = new Intent(c,Choice_Activity.class);
                    c.startActivity(i);
                }
                pd.dismiss();

            }
        }
        /*
            public Task<List<Document>> then(@NonNull Task<RemoteUpdateResult> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e("STITCH", "Update failed!");
                    throw task.getException();
                }
                List<Document> docs = new ArrayList<>();
                return coll
                        .find(new Document("owner_id", client.getAuth().getUser().getId()))
                        .limit(100)
                        .into(docs);
            }
        }).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    Log.d("STITCH", "Found docs: " + task.getResult().toString());
                    return;
                }
                Log.e("STITCH", "Error: " + task.getException().toString());
                task.getException().printStackTrace();
            }
        });*/
