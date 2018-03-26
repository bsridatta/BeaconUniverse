package com.estimote.proximitycontent;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.cloud.model.Color;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.proximitycontent.estimote.EstimoteCloudBeaconDetails;
import com.estimote.proximitycontent.estimote.EstimoteCloudBeaconDetailsFactory;
import com.estimote.proximitycontent.estimote.NearestBeaconManager;
import com.estimote.proximitycontent.estimote.ProximityContentManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.estimote.coresdk.observation.region.RegionUtils.computeAccuracy;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static int status = 0;
    private FirebaseFirestore db;

    private ImageView image;

    private static final Map<Color, Integer> BACKGROUND_COLORS = new HashMap<>();

    static {
        BACKGROUND_COLORS.put(Color.ICY_MARSHMALLOW, android.graphics.Color.rgb(109, 170, 199));
        BACKGROUND_COLORS.put(Color.BLUEBERRY_PIE, android.graphics.Color.rgb(98, 84, 158));
        BACKGROUND_COLORS.put(Color.MINT_COCKTAIL, android.graphics.Color.rgb(155, 186, 160));
    }

    private static final int BACKGROUND_COLOR_NEUTRAL = android.graphics.Color.rgb(160, 169, 172);

    private ProximityContentManager proximityContentManager;


    // Array of strings for ListView Title
    String[] listviewTitle = new String[]{
            "60% off", "45% off", "30% off", "35% off",
            "20% off"
    };


    int[] listviewImage = new int[]{
            R.drawable.kids1, R.drawable.kids2, R.drawable.kids3, R.drawable.kids4,
            R.drawable.kids5
    };

    String[] listviewShortDescription = new String[]{
            "Boys casual  T-Shirts", " Girls Party Wear", "Full body Jump Suits", "Girls Tops special",
            "Summer Night Wear"
    };

    private ListView androidListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        androidListView = (ListView) findViewById(R.id.list_view);

        androidListView.setVisibility(View.INVISIBLE);

        image = (ImageView) findViewById(R.id.imageView);
        //firestore
        // Access a Cloud Firestore instance from your Activity
        // Write a message to the database
        db= FirebaseFirestore.getInstance();

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                showNotification();
                return true;
            }
        });



        List<String> devices=Arrays.asList(
                "2e7ab6c8f349a34ef9db1c67956bba10",
                "4581c7f42cb674ef50861afd0530db03",
                "799216f2968aeb992c55f7c0e7b19111");

        NearestBeaconManager nearestBeaconManager= new NearestBeaconManager(this,devices);


        proximityContentManager = new ProximityContentManager(this,
                Arrays.asList(
                        "2e7ab6c8f349a34ef9db1c67956bba10",
                        "4581c7f42cb674ef50861afd0530db03",
                        "799216f2968aeb992c55f7c0e7b19111"),
                new EstimoteCloudBeaconDetailsFactory());
        proximityContentManager.setListener(new ProximityContentManager.Listener() {
            @Override
            public void onContentChanged(Object content) {
                String text;
                Integer backgroundColor;
                if (content != null || status==1) {
                    EstimoteCloudBeaconDetails beaconDetails = (EstimoteCloudBeaconDetails) content;
                    //text = "You're in " + beaconDetails.getBeaconName() + "'s range!";
                    text = "You're in the Kids Section!";
                    backgroundColor = BACKGROUND_COLORS.get(beaconDetails.getBeaconColor());

                    // Add a new document in collection "cities"

                    // Create a new user with a first and last name
                    Map<String, Object> user = new HashMap<>();
                    user.put("Name", "User21");
                    user.put("In", "13 March 2018 at 12:10:10 UTC+5:30");
                    user.put("Out",null);


                    // Add a new document with a generated

                    db.collection("Users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }


                else {
                    text = "No beacons in range.";
                    backgroundColor = null;
                }
                ((TextView) findViewById(R.id.textView)).setText(text);
                findViewById(R.id.relativeLayout).setBackgroundColor(
                        backgroundColor != null ? backgroundColor : BACKGROUND_COLOR_NEUTRAL);


            }
        });




        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 4; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", listviewTitle[i]);
            hm.put("listview_discription", listviewShortDescription[i]);
            hm.put("listview_image", Integer.toString(listviewImage[i]));
            aList.add(hm);
        }

        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
        androidListView.setAdapter(simpleAdapter);

    }

    private void showNotification() {

        status=1;

        ///
        ///

        String text;
        Integer backgroundColor;
        if (status==1) {

            //******************
            // for simulation purposes


            //text = "You're in " + beaconDetails.getBeaconName() + "'s range!";
            text = "You're in the Kids Section!";

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // yourMethod();
                    setList();
                }
            }, 2000);




            // Add a new document in collection "cities"

            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("Name", "User21");
            user.put("In", "3 March 2018 at 12:10:10 UTC+5:30");
            user.put("Out",null);


            // Add a new document with a generated

            db.collection("Users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }


        else {
            text = "No beacons in range.";
            backgroundColor = null;
            androidListView.setVisibility(View.INVISIBLE);

        }
        ((TextView) findViewById(R.id.textView)).setText(text);

    }

    private void setList() {
        androidListView.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met");
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else {
            Log.d(TAG, "Starting ProximityContentManager content updates");
            proximityContentManager.startContentUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Stopping ProximityContentManager content updates");
        proximityContentManager.stopContentUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proximityContentManager.destroy();
    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            status=0;


          Toast.makeText(MainActivity.this,"Zeroing",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }



























//                    EstimoteLocation e = new EstimoteLocation() {
//                        @NotNull
//                        @Override
//                        public String getDeviceId() {
//                            return null;
//                        }
//
//                        @Override
//                        public int getChannel() {
//                            return 0;
//                        }
//
//                        @Override
//                        public int getProtocolVersion() {
//                            return 0;
//                        }
//
//                        @Override
//                        public int getMeasuredPower() {
//                            return 0;
//                        }
//
//                        @NotNull
//                        @Override
//                        public MacAddress getMacAddress() {
//                            return null;
//                        }
//
//                        @Override
//                        public int getRssi() {
//                            return 0;
//                        }
//
//                        @Override
//                        public long getTimestamp() {
//                            return 0;
//                        }
//                    };
//                    e.getRssi();
}
