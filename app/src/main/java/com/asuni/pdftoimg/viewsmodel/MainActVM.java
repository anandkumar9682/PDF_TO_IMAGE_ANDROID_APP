package com.asuni.pdftoimg.viewsmodel;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Environment;

import android.os.ParcelFileDescriptor;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;

import com.asuni.pdftoimg.R;
import com.asuni.pdftoimg.adapter.FieldNameSelectAdapter;
import com.asuni.pdftoimg.views.MainAct;
import com.google.android.material.snackbar.Snackbar;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActVM {

    static MainAct mainAct;


    public MainActVM(MainAct mainAct){

        this.mainAct = mainAct;

        snackbar = Snackbar.make(mainAct.mainactBinding.getRoot(),"",Snackbar.LENGTH_INDEFINITE);

        if( checkWriteExternalPermission() )
            new LoadPdf().execute(true);
        else
            mainAct.toastMessage("Please Give permission and Restart App");


    }

    private boolean checkWriteExternalPermission() {

        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = mainAct.checkCallingOrSelfPermission( permission );
        return ( res == PackageManager.PERMISSION_GRANTED );

    }

    public List<File > fileList=new ArrayList<>();
    public List<String >fileNameList=new ArrayList<>();

    int i=0;

    public void must( File f ){

       if ( f.isDirectory() ) {

           for (File f1 : f.listFiles(  ))
               if ( f1.isFile() )
               {
                   if( f1.getName().toLowerCase().endsWith(".pdf")  )
                       fileList.add( f1 );
               }
               else if ( f1.isDirectory() )
                   try{
                       must( f1 );
                       System.out.println("final run : "+i++);
                   }catch (Exception e){

                       System.out.println("Exception :::::::::::::");

                   }
       }
    }

    public class LoadPdf extends AsyncTask<Boolean , Integer, Integer> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(mainAct,R.style.MyAlertDialogStyle);
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected Integer doInBackground(Boolean... b) {

            File dir = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                dir = Environment.getExternalStoragePublicDirectory("");
            else
                dir = new File(String.valueOf(Environment.getExternalStorageDirectory()));


            try{
                must( dir );
            }catch (Exception e){ System.out.println( e );}

            for ( File file : fileList )
                fileNameList.add( file.getName()  );


            return 1;
        }

        @Override
        protected void onPostExecute(Integer file) {
            super.onPostExecute(file);

            if( fileList.isEmpty()) {
                mainAct.mainactBinding.header.setText("Not Found PDF File");
                mainAct.mainactBinding.header.setTextColor(Color.RED);
            }


            ListView listView= mainAct.findViewById(R.id.list);
            listView.setAdapter(new FieldNameSelectAdapter( MainActVM.this , mainAct , fileNameList ,fileList ) );


            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if( snackbar!=null)
                        snackbar.dismiss();

                    return true;
                }
            });

            progressDialog.dismiss();

        }
    }

    Snackbar snackbar;

    public void showConfirmPopup( File file ){

        View customSnackView = mainAct.getLayoutInflater().inflate(R.layout.confirm_document_tool, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        snackbarLayout.setPadding(0, 0, 0, 0);

        customSnackView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();

                new CreatePdfTask().execute( file );
            }
        });

        snackbarLayout.addView(customSnackView, 0);

        snackbar.show();

    }


    public static class CreatePdfTask extends AsyncTask<File , Integer, Integer> {

        ProgressDialog progressDialog;

        @SuppressLint("WrongThread")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog( mainAct ,R.style.MyAlertDialogStyle);
            progressDialog.setTitle("Progress. Please wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected Integer doInBackground(File... files) {

            try {
                List<Bitmap> list=renderToBitmap( files[0] );

                for( int i=0 ; i<list.size() ; i++){
                    saveImages( files[0].getName() , list.get(i) );
                }

            } catch (Exception e) { }
            return 1;
        }

        int pageCount;

        public  List<Bitmap> renderToBitmap(File f) {

            List<Bitmap> images = new ArrayList<>();
            PdfiumCore pdfiumCore = new PdfiumCore(mainAct.getApplicationContext());
            try {
                ParcelFileDescriptor fd = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfDocument pdfDocument = pdfiumCore.newDocument(fd);

                pageCount = pdfiumCore.getPageCount(pdfDocument);

                for (int i = 0; i < pageCount; i++) {

                    pdfiumCore.openPage(pdfDocument, i);
                    int width = pdfiumCore.getPageWidthPoint(pdfDocument, i);
                    int height = pdfiumCore.getPageHeightPoint(pdfDocument, i);
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    pdfiumCore.renderPageBitmap(pdfDocument, bmp, i, 0, 0, width, height);
                    images.add(bmp);

                    publishProgress(i);

                }
                pdfiumCore.closeDocument(pdfDocument);
            } catch(Exception e) {
            }
            return images;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);
            this.progressDialog.setProgress(((values[0] + 1) * 100) /pageCount);
            StringBuilder sb = new StringBuilder();
            sb.append("Processing images (");
            sb.append(values[0] + 1);
            sb.append("/");
            sb.append(pageCount);
            sb.append(")");
            progressDialog.setTitle(sb.toString());

        }

        @Override
        protected void onPostExecute(Integer file) {
            super.onPostExecute(file);
            mainAct.toastMessage("Conversion Successfully.");

            mainAct.showAlert1();

            progressDialog.dismiss();

        }

    }




    public static File saveImages( String folderName, Bitmap bitmap){
        String imageFileName = "Image" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory("Image 2 PDF Converter/"+folderName);

        if (!storageDir.exists())
            new File(storageDir.getPath()).mkdirs();


        File image=null;
        try {
            image= File.createTempFile(imageFileName, ".png", storageDir);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(image));
        } catch (Exception e1) {  }

        return image;
    }


}
