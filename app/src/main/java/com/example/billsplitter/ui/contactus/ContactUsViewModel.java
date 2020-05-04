package com.example.billsplitter.ui.contactus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ContactUsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ContactUsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("You can contact us at our email id billsplitter@gmail.com");
    }

    public LiveData<String> getText() {
        return mText;
    }
}