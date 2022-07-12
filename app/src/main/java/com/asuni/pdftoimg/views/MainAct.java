package com.asuni.pdftoimg.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import com.asuni.pdftoimg.databinding.MainactBinding;
import com.asuni.pdftoimg.viewsmodel.MainActVM;
import com.asuni.pdftoimg.R;
import com.asuni.pdftoimg.viewsmodel.MainActivity;
import com.google.android.material.navigation.NavigationView;

public class MainAct extends AppCompatActivity {

    MainActVM mainActVM;

    public MainactBinding mainactBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        mainactBinding= DataBindingUtil.setContentView(this , R.layout.mainact);
        mainActVM = new MainActVM( this );
        mainactBinding.setViewModel(mainActVM);



        mainactBinding.list.setScrollingCacheEnabled(false);

        DrawerLayout drawerLayout = findViewById( R.id.drawer );
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout , R.string.nav_open , R.string.nav_close );

        drawerLayout.addDrawerListener( actionBarDrawerToggle ) ;
        actionBarDrawerToggle.syncState() ;

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar ) ;

        drawerLayout.close();

        findViewById(R.id.menu_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        findViewById(R.id.choosefile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                startActivityForResult(  intent ,1 );


            }
        });

        NavigationView navigationView=findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                drawerLayout.close();

                switch ( item.getItemId() ) {

                    case R.id.contact: {
                        startActivity(new Intent(getApplicationContext(), ContactUS.class));
                        return true;
                    }

                    case R.id.show_us_love: {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.asuni.pdftoimg")));
                        return true;
                    }


                }
                return true;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1)
            if (resultCode == RESULT_OK) {

                if( mainActVM.fileNameList.contains( getFileName( data.getData() )) ){

                    mainActVM.showConfirmPopup( mainActVM.fileList.get(  mainActVM.fileNameList.indexOf( getFileName(data.getData()) )) );

                }
            }



    }


    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void showAlert1( ){

        AlertDialog.Builder alertBuiler= new AlertDialog.Builder(MainAct.this);
        alertBuiler.setTitle("Where my images store?");
        alertBuiler.setMessage("Internal storage --> Image 2 PDF Converter folder --> in check folder name same your pdf file name. all images store that folder.");

        alertBuiler.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AlertDialog alertDialog = alertBuiler.create();
                alertDialog.cancel();

            }
        });

        alertBuiler.show();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public void toastMessage(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

}