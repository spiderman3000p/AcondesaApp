package acondesa.acondesaapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class UbicationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static boolean LOCATION_PERMISSION_GRANTED = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubication);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(10.9275116, -74.7766533);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(sydney).snippet("Cr 30 28 A-180 Hipódromo (Soledad), Atlántico");
        markerOptions.position(sydney).title("ACONDESA");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,getString(R.string.ask_perrmission_location_rationale));
           // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
            if(LOCATION_PERMISSION_GRANTED)
                mMap.setMyLocationEnabled(true);
        }else {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void requestPermission(String permission,String message) {


        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Importante");
            dialog.setMessage(message);
            dialog.setCancelable(false);

            dialog.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //ir a la pantalla de configuracion de aplicacion
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null)));
                }//do some
            });
            dialog.setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(UbicationActivity.this, getText(R.string.denied_location_permission_message), Toast.LENGTH_LONG).show();
                }//do some
            });
            dialog.show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // si el permiso es concedido
                    LOCATION_PERMISSION_GRANTED = true;
                    Toast.makeText(this, getText(R.string.granted_location_permission_message), Toast.LENGTH_LONG).show();
                } else {
                    //sino se concede el permiso
                    LOCATION_PERMISSION_GRANTED = false;
                    Toast.makeText(this, getText(R.string.denied_location_permission_message), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            LOCATION_PERMISSION_GRANTED = true;
            return true;
        } else {
            LOCATION_PERMISSION_GRANTED = false;
            return false;
        }
    }
}
