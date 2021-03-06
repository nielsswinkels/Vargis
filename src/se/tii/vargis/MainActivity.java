package se.tii.vargis;

import java.io.IOException;
import java.util.HashMap;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnQRCodeReadListener {
	private TextView mTextView;
	private Button mediaButton;
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	/*
	 * private IntentFilter[] mIntentFilters; private String[][] mNFCTechLists;
	 */
	private MediaPlayer mediaPlayer;
	private HashMap<String, Integer> tagSounds = new HashMap<String, Integer>();
	private HashMap<String, Integer> codeSounds = new HashMap<String, Integer>();
	private QRCodeReaderView mydecoderview;
	private int lastSound = 0;

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		Log.d(getString(R.string.tag), "onCreate()");

		tagSounds.put("ae 66 c0 aa ", R.raw.urbanum1_sv);
		tagSounds.put("6e 7f bf aa ", R.raw.urbanum2_sv);
		tagSounds.put("e4 6e 06 0c ", R.raw.urbanum2_sv); //round sticker
		tagSounds.put("cf 07 df 10 00 01 04 e0 ", R.raw.urbanum3_sv);

		codeSounds.put("gsm:urb:001", R.raw.urbanum1_sv);
		codeSounds.put("gsm:urb:002", R.raw.urbanum2_sv);
		codeSounds.put("gsm:urb:003", R.raw.urbanum3_sv);

		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.tv);
		mediaButton = (Button) findViewById(R.id.button);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
		mydecoderview.setOnQRCodeReadListener(this);

		if (mNfcAdapter != null) {
			mTextView.setText("Read an NFC tag");
		} else {
			mTextView.setText("This phone is not NFC enabled.");
		}

		// create an intent with tag data and deliver to this activity
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		mediaButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
						((Button) v).setText("Start");
						try {
							mediaPlayer.prepare();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						mediaPlayer.start();
						((Button) v).setText("Stop");
					}
				}
			}
		});
	}

	@Override
	public void onNewIntent(Intent intent) {

		Log.d(getString(R.string.tag), "onNewIntent()");
		String action = intent.getAction();
		Log.d(getString(R.string.tag), "intent type=" + intent.getType());

		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		// probably there is a better way to do this part
		byte[] tagIdByte = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
		String tagId = new String();
		for (int i = 0; i < tagIdByte.length; i++) {
			String x = Integer.toHexString(((int) tagIdByte[i] & 0xff));
			if (x.length() == 1) {
				x = '0' + x;
			}
			tagId += x + ' ';
		}
		Log.d("tagId", tagId);

		String s = action + "\n\n" + tag.toString() + " id:" + tagId;

		foundTag(tagId);

		mTextView.setText(s);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(getString(R.string.tag), "onResume");

		// mydecoderview.surfaceCreated(mydecoderview.getHolder());
		// mydecoderview.getCameraManager().getCamera().open();
		mydecoderview.getCameraManager().startPreview();

		if (mNfcAdapter != null)
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
		// mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,
		// mIntentFilters, mNFCTechLists);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(getString(R.string.tag), "onPause");

		mydecoderview.getCameraManager().stopPreview();

		if (mNfcAdapter != null)
			mNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		Camera camera = mydecoderview.getCameraManager().getCamera();
		if (camera != null)
			camera.release();
	}

	private void playSound(int fileName) {
		Log.d(getString(R.string.tag), "Playing sound");
		if (mediaPlayer != null && mediaPlayer.isPlaying())
			mediaPlayer.stop();
		// mpintro = MediaPlayer.create(this,
		// Uri.parse(Environment.getExternalStorageDirectory().getPath()+
		// "/Music/intro.mp3"));
		mediaPlayer = MediaPlayer.create(this, fileName);
		mediaPlayer.setLooping(false);
		mediaPlayer.start();
		mediaButton.setText("Stop");
		lastSound = fileName;
	}

	// used when calling zxing via intent with result
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 25, 400);
				toast.show();
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Toast toast = Toast.makeText(this, "Scan was Cancelled!", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 25, 400);
				toast.show();

			}
		}
	}

	// Called when there's no QR codes in the camera preview image
	@Override
	public void QRCodeNotFoundOnCamImage() {
		// TODO Auto-generated method stub

	}

	// Called when your device have no camera
	@Override
	public void cameraNotFound() {
		// TODO Auto-generated method stub

	}

	// Called when a QR is decoded
	// "text" : the text encoded in QR
	// "points" : points where QR control points are placed
	@Override
	public void onQRCodeRead(String text, PointF[] points) {
		foundCode(text);
		mTextView.setText(text);
		for (PointF pointF : points) {
			Log.d("points", "x:" + pointF.x + " y:" + pointF.y);
		}

	}

	public void foundTag(String tagId) {
		if (tagSounds.containsKey(tagId)) {
			Log.d(getString(R.string.tag), "match tag");
			playSound(tagSounds.get(tagId));
		} else {
			Log.d(getString(R.string.tag), "NO MATCH <" + tagId + ">");
		}
	}

	public void foundCode(String code) {

		if (codeSounds.containsKey(code)) {
			Log.d(getString(R.string.tag), "match code");
			if (!(mediaPlayer != null && mediaPlayer.isPlaying() && lastSound == codeSounds.get(code)))
				playSound(codeSounds.get(code));
		} else {
			Log.d(getString(R.string.tag), "NO MATCH <" + code + ">");
		}
	}

	private void storePreferenceInt(String tag, int value) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(tag, value);
		editor.commit();
	}
}