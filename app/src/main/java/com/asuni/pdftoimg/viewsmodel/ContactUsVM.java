package com.asuni.pdftoimg.viewsmodel;

import android.content.Intent;
import android.net.Uri;

import com.asuni.pdftoimg.views.ContactUS;


public class ContactUsVM {


  ContactUS contactUS;

  public ContactUsVM(ContactUS contactUS) {
    this.contactUS=contactUS;

  }

  public void openBox(int op ){

    if( op == 1 ){


      Intent email = new Intent(Intent.ACTION_SEND);
      email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "asuni.docs@gmail.com" } ) ;
      email.putExtra(Intent.EXTRA_SUBJECT, "Contact Query : from CSV Application");
      email.setType("message/rfc822");

      contactUS.startActivity(Intent.createChooser(email, "Choose an Email client :"));


    }
    else if( op ==2 ){

      contactUS.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://asuni.in")));

    }

  }


}
