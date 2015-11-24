package se.tii.vargis;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.TextView;

public class QRReaderActivity extends Activity implements OnQRCodeReadListener {

	private TextView myTextView;
    private QRCodeReaderView mydecoderview;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

        myTextView = (TextView) findViewById(R.id.qrText);
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
        myTextView.setText(text);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }
}
