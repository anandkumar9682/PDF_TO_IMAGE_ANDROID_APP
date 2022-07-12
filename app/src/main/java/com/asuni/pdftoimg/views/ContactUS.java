package com.asuni.pdftoimg.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.asuni.pdftoimg.R;
import com.asuni.pdftoimg.databinding.ActContactUsBinding;
import com.asuni.pdftoimg.viewsmodel.ContactUsVM;

public class ContactUS extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActContactUsBinding actContactUsBinding = DataBindingUtil.setContentView( this , R.layout.act_contact_us);
    actContactUsBinding.setViewModel( new ContactUsVM(this) );
  }



}
