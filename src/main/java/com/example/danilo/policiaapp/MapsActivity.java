package com.example.danilo.policiaapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
                                                               GoogleApiClient.ConnectionCallbacks,
                                                               GoogleApiClient.OnConnectionFailedListener,
                                                               com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    //private LocationManager locationManager;
    private static final String TAG = "MapsActivity";
    private GoogleApiClient mGoogleApiClient;
    private TextView aviso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        aviso = (TextView) findViewById(R.id.tvAviso);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart(){
        super.onStart();
        //Conecta ao Google Play Services
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop(){
        //Para o GPS
        stopLocationUpdates();
        //Desconecta ao Google Play Services
        mGoogleApiClient.disconnect();
        super.onStop();
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

        try {
            Log.d(TAG, "OnMapReady: "+ googleMap);
            this.mMap = googleMap;
            //Configura o tipo do mapa
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


            //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //Criteria criteria = new Criteria();
            //String provider = locationManager.getBestProvider(criteria, true);
            //mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);
            //mMap.setMyLocationEnabled(true);

           // LatLng latlong = new LatLng(locationManager.getLastKnownLocation(provider).getLatitude(), locationManager.getLastKnownLocation(provider).getLongitude());
           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16.0f));
        }catch (SecurityException ex)
        {
            Log.e(TAG, "Error", ex);
        }

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-18.918232, -48.258208);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("UFU - Universidade Federal de Uberlândia"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open:
                Intent intent = new Intent(this, ActivityAbreOcorrencia.class);
                startActivity(intent);
                return true;

            case R.id.action_cancel:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //Conectado ao Google Play Service!
        //Podemos utilizar qualquer API agora.
        //toast("Conectado ao Google Play Services!");
        //Inicia o GPS
        startLocationUpdates();
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        setMapLocation(l);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //A concexão foi interrompida.
        //A aplicação precisa aguardar até a conexão ser restabelecida
        toast("Conexão interrompida.");
    }


    @Override
    public void onConnectionFailed(ConnectionResult result){
        //Erro de conexão
        //Pode ser uma configuração inválida ou falta de conectividade no dispositivo
        toast("Erro ao conectar: "+result);
    }

    private void toast(String s) {
        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
    }

    //Atualiza a coordenada do mapa
    private void setMapLocation(Location l){
        if(mMap != null && l != null){
            LatLng latlng = new LatLng(l.getLatitude(), l.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, 15);
            mMap.animateCamera(update);
            Log.d(TAG, "setMapLocation: "+l);

            CircleOptions circle = new CircleOptions().center(latlng);
            circle.fillColor(Color.BLUE);
            circle.radius(25); //Em metros
            mMap.clear();
            mMap.addCircle(circle);
        }
    }

    protected void startLocationUpdates(){
        Log.d(TAG, "startLocationUpdates()");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    protected void stopLocationUpdates(){
        Log.d(TAG, "stopLocationUpdates()");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged(): "+location);
        setMapLocation(location);
    }
}
