package com.example.lendapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.Html;
import androidx.appcompat.app.AppCompatActivity;

public class AuthorizationDescription extends AppCompatActivity {

    private static final String TAG = "AuthorizationDesc";

    private Button agreeButton, disagreeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization_description);

        // Initialize the buttons
        agreeButton = findViewById(R.id.agree_button);
        disagreeButton = findViewById(R.id.disagree_button);

        // Ensure the button is not null
        if (agreeButton == null) {
            Log.e(TAG, "agreeButton is null. Check the ID in authorization_description.xml");
            return;
        }

        // Ensure the button is not null
        if (disagreeButton == null) {
            Log.e(TAG, "disagreeButton is null. Check the ID in authorization_description.xml");
            return;
        }

        TextView descriptionText = findViewById(R.id.description_text);
        if (descriptionText == null) {
            Log.e(TAG, "descriptionText is null. Check the ID in authorization_description.xml");
            return;
        }
        String text = "<br/>" +
                "যখন আপনি আমাদের LEND DO অ্যাপ ব্যবহার করেন, আমরা নিম্নলিখিত ধরণের ব্যক্তিগত তথ্য সংগ্রহ করতে পারি:<br/><br/>" +
                "<b><big>SMS বার্তা:</big></b><br/>" +
                "আপনার আর্থিক ইতিহাস যাচাই করার জন্য এবং ঋণ আবেদন প্রক্রিয়া সুবিধাজনক করার জন্য আমরা আপনার SMS বার্তাগুলি অ্যাক্সেস করার অনুরোধ করতে পারি। আমাদের অ্যাপের উদ্দেশ্যে সম্পর্কিত নয় এমন কোনো ব্যক্তিগত বার্তা আমরা অ্যাক্সেস বা সঞ্চয় করি না।<br/><br/>" +
                "<b><big>যোগাযোগের তথ্য:</big></b><br/>" +
                "ঋণ আবেদন প্রক্রিয়া মসৃণ করার জন্য, আমরা আপনার ডিভাইসের যোগাযোগের তথ্য অ্যাক্সেস করার অনুরোধ করতে পারি যা আপনি নির্বাচন করেছেন। এটি আমাদের আপনি যে তথ্য প্রদান করেন তা যাচাই করতে এবং অ্যাপের মধ্যে সুবিধাজনক যোগাযোগের বিকল্প অফার করতে সক্ষম করে।<br/><br/>" +
                "<b><big>ইমেজ তথ্য:</big></b><br/>" +
                "আমরা ব্যবহারকারীর ধার দেওয়ার প্রক্রিয়া চলাকালীন ব্যবহারকারীর ছবি তথ্য সংগ্রহ করব, যার মধ্যে নাম, ছবির ধরন, দৈর্ঘ্য, ইত্যাদি রয়েছে, জালিয়াতি বিরোধী পরিদর্শন এবং ঋণ দেওয়ার জন্য ব্যবহারকারীর পরিচয় যাচাই করার জন্য। ব্যবহারকারীদের নিবন্ধন ফর্মটি পূরণ করতে ফটো আপলোড করার অনুমতি দিন। সংগৃহীত তথ্য এনক্রিপ্ট করা হবে।<br/><br/>" +
                "<b><big>ক্যামেরা:</big></b><br/>" +
                "কিছু ক্ষেত্রে, আমরা আপনার ডিভাইসের ক্যামেরায় অ্যাক্সেসের অনুরোধ করতে পারি যাতে ঋণের আবেদনের জন্য নথি স্ক্যান করার মতো বৈশিষ্ট্যগুলি সক্ষম করা যায়। আমরা ক্যামেরার মাধ্যমে ধারণ করা ছবি বা ভিডিও সংরক্ষণ করি না যদি না আপনি স্পষ্টভাবে অনুমোদন করেন।<br/><br/>" +
                "<b><big>অবস্থান তথ্য:</big></b><br/>" +
                "আপনার সম্মতি সাপেক্ষে, আমরা আপনার ডিভাইসের অবস্থান তথ্য সংগ্রহ এবং ব্যবহার করতে পারি। এটি আমাদের অবস্থান-ভিত্তিক ঋণ প্রস্তাবের জন্য আপনার যোগ্যতা মূল্যায়ন করতে এবং আপনাকে সম্পর্কিত পরিষেবা প্রদান করতে সাহায্য করে।<br/><br/>" +
                "<b><big>ডিভাইস তথ্য:</big></b><br/>" +
                "আমরা নির্দিষ্ট ডিভাইস তথ্য সংগ্রহ করি, যার মধ্যে ডিভাইসের ধরণ, অপারেটিং সিস্টেমের সংস্করণ, অনন্য ডিভাইস শনাক্তকারী, এবং মোবাইল নেটওয়ার্কের তথ্য অন্তর্ভুক্ত। এটি আমাদের অ্যাপের আদর্শ পারফরম্যান্স নিশ্চিত করতে, প্রযুক্তিগত সমস্যা সমাধান করতে, এবং ব্যক্তিগতকৃত ব্যবহারকারী অভিজ্ঞতা প্রদান করতে সাহায্য করে।<br/><br/>";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            descriptionText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            descriptionText.setText(Html.fromHtml(text));
        }

        // Set onClickListener for agree button
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Navigate to the Home activity
                    Intent intent = new Intent(AuthorizationDescription.this, Home.class);
                    startActivity(intent);
                    finish(); // Finish the current activity
                } catch (Exception e) {
                    // Log any errors that occur during navigation
                    Log.e("AuthorizationDesc", "Error starting Home activity", e);
                }
            }
        });

        // Set onClickListener for disagree button
        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Disagree button clicked. Exiting the app.");
                System.exit(0);
            }
        });
    }
}
