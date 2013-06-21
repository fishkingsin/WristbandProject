package com.idthk.wristband.socialnetwork;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import com.idthk.wristband.ui.Main;
import com.idthk.wristband.ui.R;
import com.idthk.wristband.ui.MainFragment;
//import com.idthk.wristband.R.id;
//import com.idthk.wristband.R.layout;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class FacebookShareActivity extends Activity implements OnCancelListener{

	private Button LoginButton;
	private Button LogoutButton;
	private Button PostPhotoButton;
	private Button PostTextButton;
	ProgressHUD mProgressHUD; 
    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
    private PendingAction pendingAction = PendingAction.NONE;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //handle the callback of Facebook
            onSessionStateChange(session, state, exception);
            Log.d("Facebook", "Callback");
        }
    };
	
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (pendingAction != PendingAction.NONE && (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) { 
        	//If the user wants to post photo or update status but the permission is not granted by user
        	new AlertDialog.Builder(FacebookShareActivity.this)
                    .setTitle("Fail")
                    .setMessage("Unable to perform selected action because permissions were not granted.")
                    .setPositiveButton("Ok", null)
                    .show();
            pendingAction = PendingAction.NONE;
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            handlePendingAction();
        }
    }
    
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_PHOTO:
                postPhoto();
                break;
            case POST_STATUS_UPDATE:
                postText();
                break;
			case NONE:
				//Do Nothing
				break;
			default:
				break;
        }
    }
    
	@SuppressWarnings("unused")
	private void getHashKey(){
		//Use to retrieve the hash key needed for the facebook app. Compiling and the actual apk have different hash key.
	    PackageInfo info;
	    try {
	        info = getPackageManager().getPackageInfo("com.example.socialnetwork", PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md;
	            md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            String something = new String(Base64.encode(md.digest(), 0));
	            Log.e("hash key", something);
	        }
	    } catch (NameNotFoundException e1) {
	        Log.e("name not found", e1.toString());
	    } catch (NoSuchAlgorithmException e) {
	        Log.e("no such an algorithm", e.toString());
	    } catch (Exception e) {
	        Log.e("exception", e.toString());
	    }
	}
	
	private void facebookLogin(){
		Log.d("Facebook", "Start Facebook Login");
		Session.openActiveSession(this, true, new Session.StatusCallback() {
			 
			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					// make request to the /me API
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

						// callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user != null) {
								Log.d("Facebook", "Login Sucessful with Username:" + user.getName());
							}
						}
					});
				}
			}
		});
	}
	
	private void facebookLogout(){
		Log.d("Facebook", "Start Facebook Logout");
	    if (Session.getActiveSession() != null) {
	        Session.getActiveSession().closeAndClearTokenInformation();
	    }

	    Session.setActiveSession(null);
	    Log.d("Facebook", "Logout");

	}
	
    private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE);
    }
    
    private void onClickPostPhoto() {
        performPublish(PendingAction.POST_PHOTO);
    }
	
	private void postPhoto(){
		Bitmap bitmap;
		View v1 = LoginButton;
		v1.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);
		
		
		if (hasPublishPermission()) {
			mProgressHUD = ProgressHUD.show(FacebookShareActivity.this,"Posting", true,true,this);
            Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), bitmap, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    showPublishResult("Photo Post", response.getGraphObject(), response.getError());
                }
            });
            request.executeAsync();
        }
	}
	
	private void postText(){
        if (hasPublishPermission()) {

			mProgressHUD = ProgressHUD.show(FacebookShareActivity.this,"Posting", true,true,this);
            final String message = ((EditText) findViewById(R.id.facebook_share_textfield)).getText().toString();
            Request request = Request
                    .newStatusUpdateRequest(Session.getActiveSession(), message, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            showPublishResult(message, response.getGraphObject(), response.getError());
                        }
                    });
            request.executeAsync();
        }
	}
	
    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = "Success";
            alertMessage = "Sucessfully posted";
            mProgressHUD.setMessage("Completed");

            mProgressHUD.dismiss();
        } else {
            title = "Error";
            alertMessage = error.getErrorMessage();
            mProgressHUD.setMessage("Error");

            mProgressHUD.dismiss();
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton("ok", null)
                .show();
        
        pendingAction = PendingAction.NONE;
    }
    
    private void performPublish(PendingAction action) {
        Session session = Session.getActiveSession();

        pendingAction = action;
        if (session != null) {
            if (session.isOpened()){
                if (hasPublishPermission()) {
                    // We can do the action right away.
                    handlePendingAction();
                } else {
                    // We need to get new permissions, then complete the action when we get called back.
                	session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSIONS));
                }
        	}
        	else {
        		facebookLogin();
        	}
        }
        else {
        	facebookLogin();
        }
    }
    
    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_share_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String title = intent.getStringExtra(Main.TITLE);
        String contentString = intent.getStringExtra(MainFragment.FACEBOOK);
        ((TextView)findViewById(R.id.titlebar_textview)).setText(title);
        EditText editText = (EditText) findViewById(R.id.facebook_share_textfield);
        editText.append(contentString);
        
        ((Button) findViewById(R.id.btn_settings_done)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        
		LoginButton = (Button) findViewById(R.id.buttonLogin);
		LoginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.d("Click", "Did Click");
                facebookLogin();
            }
        });
		
		LogoutButton = (Button) findViewById(R.id.buttonLogout);
		LogoutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.d("Click", "Did Click");
                facebookLogout();
            }
        });
		
		PostPhotoButton = (Button) findViewById(R.id.buttonPostPhoto);
		PostPhotoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.d("Click", "Did Click");
				onClickPostPhoto();
            }
        });
		
		PostTextButton = (Button) findViewById(R.id.buttonPostText);
		PostTextButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.d("Click", "Did Click");
				onClickPostStatusUpdate();
            }
        });
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
		//Return Facebook Login Activity
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		handlePendingAction();

	}

	
	@Override
	public void onCancel(DialogInterface arg0) {
		// TODO Auto-generated method stub
		mProgressHUD.dismiss();
	}
	
}
