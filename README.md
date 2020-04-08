# PhoneInput
Phone input view for android based on <a href="https://github.com/lamudi-gmbh/android-phone-field">android-phone-field</a>

## gradle

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	dependencies {
	        implementation 'com.github.mutasemhajhasan:PhoneInput:1.0.0'
	}
  
## activity.xml
```xml
     <me.mutasem.phoneinput.PhoneInputLayout
        android:id="@+id/phoneInputLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

    <me.mutasem.phoneinput.PhoneEditText
        android:id="@+id/phoneEditText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```
## activity.java
```java
     phoneEditText = findViewById(R.id.phoneEditText);
     phoneInputLayout = findViewById(R.id.phoneInputLayout);
     //set default selected country
     phoneEditText.setDefaultCountry("SY");
     phoneInputLayout.setDefaultCountry("SY");
     //to get the phone number
     //String phone=phoneEditText.getPhoneNumber();
     submit = findViewById(R.id.submit);
     submit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             //validate phone number
             if (phoneEditText.isValid() && phoneInputLayout.isValid())
                 Toast.makeText(MainActivity.this, phoneEditText.getPhoneNumber() + "," + phoneInputLayout.getPhoneNumber(), Toast.LENGTH_LONG).show();
             else
                 Toast.makeText(MainActivity.this, "Invalid phone", Toast.LENGTH_SHORT).show();
         }
     });
```
    

        
