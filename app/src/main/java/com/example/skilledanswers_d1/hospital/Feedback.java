package com.example.skilledanswers_d1.hospital;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Feedback extends AppCompatActivity {

    private TextInputLayout inputLayoutname=null;
    private TextInputLayout  inputLayoutroomno=null;
    private TextInputLayout inputLayoutmobileno=null;
    private TextInputLayout inputLayoutemail=null;
    private EditText editTextname=null;
    private EditText editTextroomno=null;
    private EditText editTextmobileno=null;
    private EditText editTextemail=null;
    private Button  buttonsignup=null;
    private Button buttoncancle=null;
    private int FEEDBACK_REQUEST_CODE=1;
    private String tempQ1Ans=null;
    private String tempQ2Ans=null;
    private String tempQ3Ans=null;
    private String tempQ4Ans=null;
    private String tempQ5Ans=null;
    private String tempQ6Ans=null;
    private String tempQ7Ans=null;
    private String tempComment=null;
    private DBLite   dbLite=new DBLite(this);  ///// database reference
    private BaseFont bfBold;
    private void createHeadings(PdfContentByte cb, float x, float y, String text){

        cb.beginText();
        cb.setFontAndSize(bfBold, 8);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }

    private void initializeFonts(){


        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_feedback);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Give your Identity...");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        inputLayoutname=(TextInputLayout)findViewById(R.id.input_layout_name);
        inputLayoutroomno=(TextInputLayout)findViewById(R.id.input_layout_roomno);
        inputLayoutmobileno=(TextInputLayout)findViewById(R.id.input_layout_mobile);
        inputLayoutemail=(TextInputLayout)findViewById(R.id.input_layout_email);
        ///////
        editTextname=(EditText)findViewById(R.id.input_name);
        editTextroomno=(EditText)findViewById(R.id.input_roomno);
        editTextmobileno=(EditText)findViewById(R.id.input_mobile);
        editTextemail=(EditText)findViewById(R.id.input_email);
        /////
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.milkshake);
        buttonsignup=(Button)findViewById(R.id.btn_signup);
        buttoncancle=(Button)findViewById(R.id.btn_cancle);
        buttonsignup.setAnimation(myAnim);
        buttoncancle.setAnimation(myAnim);
        editTextname.addTextChangedListener(new MyTextWatcher(editTextemail));
        editTextroomno.addTextChangedListener(new MyTextWatcher(editTextroomno));

        buttonsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(myAnim);
                submitForm();
            }
        });

        buttoncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(myAnim);
                editTextname.setText("");
                editTextroomno.setText("");
                editTextmobileno.setText("");
                editTextemail.setText("");
            }
        });
    }

    private void submitForm() {
        if (!validateName() || !validateroomno()) {

            return;

        }
        else
        {

            Intent  intent=new Intent(Feedback.this,GetFeedback.class);
            startActivityForResult(intent,FEEDBACK_REQUEST_CODE);
     }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FEEDBACK_REQUEST_CODE)
        {

            // updates the no of feedback done....
            dbLite.updateCount(dbLite.getCount().get(0).get_count(), dbLite.getCount().get(0).get_count()+1);
            System.out.println("jjjjjjjjjjjjjjjjjjjjjj " + dbLite.getCount().get(0).get_count());
            System.out.println("jjjjjjjjjjjjjjjjjjjjjjcount size " + dbLite.getCount().size());
            System.out.println("jjjjjjjjjjjjjj early-----> " + dbLite.getQ1().size());
            ///////////////////////////////////////////////// get the data
            tempQ1Ans=data.getStringExtra("Q1");
            tempQ2Ans=data.getStringExtra("Q2");
            tempQ3Ans=data.getStringExtra("Q3");
            tempQ4Ans=data.getStringExtra("Q4");
            tempQ5Ans=data.getStringExtra("Q5");
            tempQ6Ans=data.getStringExtra("Q6");
            tempQ7Ans=data.getStringExtra("Q7");
            tempComment=data.getStringExtra("COMMENT");
            /////// q1
            switch (tempQ1Ans)
            {
                case "always" : dbLite.updateQ1Always(0, dbLite.getQ1().get(0).get_always() + 1);
                    System.out.println("jjjjjjjjjjjjjj afteer-----> " + dbLite.getQ1().get(0).get_always());
                    System.out.println("jjjjjjjjjjjjjj afteerrrrr-----> " + dbLite.getQ1().get(0).get_never());
                    break;
                case "usualy":
                    dbLite.updateQ1Usualy(0, dbLite.getQ1().get(0).get_usualy() + 1);
                    break;
                case "sometimes":
                    dbLite.updateQ1Sometimes(0, dbLite.getQ1().get(0).get_sometimes() + 1);
                    break;
                case "never":
                    dbLite.updateQ1Never(0, dbLite.getQ1().get(0).get_never() + 1);
                    break;
            }
            ////// q2
            switch (tempQ2Ans)
            {
                case "always" : dbLite.updateQ2Always(0, dbLite.getQ2().get(0).get_always() + 1);
                    break;
                case "usualy":
                    dbLite.updateQ2Usualy(0, dbLite.getQ2().get(0).get_usualy() + 1);
                    break;
                case "sometimes":
                    dbLite.updateQ2Sometimes(0, dbLite.getQ2().get(0).get_sometimes() + 1);
                    break;
                case "never":
                    dbLite.updateQ2Never(0, dbLite.getQ2().get(0).get_never() + 1);
                    break;
            }
            ///// q3
            switch (tempQ3Ans)
            {
                case "always" : dbLite.updateQ3Always(0, dbLite.getQ3().get(0).get_always() + 1);
                    break;
                case "usualy":
                    dbLite.updateQ3Usualy(0, dbLite.getQ3().get(0).get_usualy() + 1);
                    break;
                case "sometimes":
                    dbLite.updateQ3Sometimes(0, dbLite.getQ3().get(0).get_sometimes() + 1);
                    break;
                case "never":
                    dbLite.updateQ3Never(0, dbLite.getQ3().get(0).get_never() + 1);
                    break;
            }
            ///// q4
            switch (tempQ4Ans)
            {
                case "always" : dbLite.updateQ4Always(0, dbLite.getQ4().get(0).get_always() + 1);
                    break;
                case "usualy":
                    dbLite.updateQ4Usualy(0, dbLite.getQ4().get(0).get_usualy() + 1);
                    break;
                case "sometimes":
                    dbLite.updateQ4Sometimes(0, dbLite.getQ4().get(0).get_sometimes() + 1);
                    break;
                case "never":
                    dbLite.updateQ4Never(0, dbLite.getQ4().get(0).get_never() + 1);
                    break;
            }
            ///// q5
            switch (tempQ5Ans)
            {
                case "always" : dbLite.updateQ5Always(0, dbLite.getQ5().get(0).get_always() + 1);
                    break;
                case "usualy":
                    dbLite.updateQ5Usualy(0, dbLite.getQ5().get(0).get_usualy() + 1);
                    break;
                case "sometimes":
                    dbLite.updateQ5Sometimes(0, dbLite.getQ5().get(0).get_sometimes() + 1);
                    break;
                case "never":
                    dbLite.updateQ5Never(0, dbLite.getQ5().get(0).get_never() + 1);
                    break;
            }
            ///// q6
            switch (tempQ6Ans)
            {
                case "always" : dbLite.updateQ6Always(0, dbLite.getQ6().get(0).get_always() + 1);
                    break;
                case "usualy":
                    dbLite.updateQ6Usualy(0, dbLite.getQ6().get(0).get_usualy() + 1);
                    break;
                case "sometimes":
                    dbLite.updateQ6Sometimes(0, dbLite.getQ6().get(0).get_sometimes() + 1);
                    break;
                case "never":
                    dbLite.updateQ6Never(0, dbLite.getQ6().get(0).get_never() + 1);
                    break;
            }
            ///// q7

            switch (tempQ7Ans)
            {
                case "Excellent" : dbLite.updateQ7Excellent(0,dbLite.getQ7().get(0).get_excellent()+1);
                    break;
                case "Very Good" : dbLite.updateQ7VeryGood(0, dbLite.getQ7().get(0).get_veryGood() + 1);
                    break;
                case "Good" : dbLite.updateQ7Good(0, dbLite.getQ7().get(0).get_good() + 1);
                    break;
                case  "Average" : dbLite.updateQ7Average(0, dbLite.getQ7().get(0).get_average() + 1);
                    break;
                case  "Poor" : dbLite.updateQ7Poor(0, dbLite.getQ7().get(0).get_poor() + 1);
                    break;
             }


String pdf="feedback"+dbLite.getCount().get(0).get_count()+".pdf";
                /////// FILE PATH TO STORE ALL THE FEEDBACKS IN THIS FOLDER......
                String FILE = Environment.getExternalStorageDirectory().toString()
                        +"/PEOPLE_TREE_SINGLE/"+pdf;
            // Create New Blank Document
                Document document= new Document(PageSize.A4);
                // Create Directory in External Storage
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/PEOPLE_TREE_SINGLE");
                myDir.mkdirs();
                // Create Pdf Writer for Writting into New Created Document
                try {
//                    PdfWriter.getInstance(document, new FileOutputStream(FILE));
//                    // Open Document for Writting into document
//                    document.open();
//
//                    // User Define Method
                    addMetadata(document);
//                    addTitlePage(document);

                    PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(FILE));
                    document.open();
                    PdfContentByte cb = docWriter.getDirectContent();
                    //initialize fonts for text printing
                    initializeFonts();
                    InputStream inputStream = getAssets().open("logo.png");
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image companyLogo = Image.getInstance(stream.toByteArray());
                    companyLogo.setAbsolutePosition(400, 700);
                    companyLogo.scalePercent(25);
                    document.add(companyLogo);
                    document.addTitle("Feedback Form Patient Food Service");

                    //creating a sample invoice with some customer data
                    createHeadings(cb, 25, 700, "Feedback Form Patient Food Service\n");
                    createHeadings(cb, 400, 780, "Sharada’s People Cafe");
                    createHeadings(cb, 400, 765, "People Tree Hospitals");
                    createHeadings(cb,400,750,"No. 2, Tumkur Service Road,");
                    createHeadings(cb,400,735,"Goregunte Palya,");
                    createHeadings(cb,400,720,"Yeshwanthpur");
                    createHeadings(cb,400,720,"Bangalore – 560022");
                    String Datetime;
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                    Datetime = dateformat.format(c.getTime());
                    createHeadings(cb,400,720,Datetime);













                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            document.close();

            //// FILE PATH TO STORE THE STATSTICS FEEDBACK.. THIS WILL BE GETTING UPDATED... AS NEW FEEDBACK COMMING...
            String FILE2=Environment.getExternalStorageDirectory().toString()+"/PEOPLE_TREE_STATSTICS/"+"STATSTCS.pdf";
            /// create the blank document
            Document document1=new Document(PageSize.A4);
            ////                // Create Directory in External Storage
            String root1 = Environment.getExternalStorageDirectory().toString();
            File myDir1 = new File(root1 + "/PEOPLE_TREE_STATSTICS");
            myDir1.mkdirs();
            // Create Pdf Writer for Writting into New Created Document
            try {
                PdfWriter.getInstance(document1, new FileOutputStream(FILE2));
                // Open Document for Writting into document
                document1.open();

                // User Define Method
                addMetaDataForStatstics(document1);
                addTitlePageStastics(document1);

            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            document1.close();
//            Toast.makeText(Feedback.this, "Thank you", Toast.LENGTH_SHORT).show();
            ///////////////////////////////////////////////////////////////////////   end of projuct
//            Intent intent=new Intent(this,ThankuActivity.class);
//            startActivity(intent);

            finish();

        }
    }

    private void addMetadata(Document document)
    {
// Open Document for Writting into document
        document.open();
        document.addTitle("PEOPLE TREE CANTEEN FEEDBACK FORM");
//        document.addSubject("Name -->"+editTextname.getText().toString()+"\n"+
//                "Room no-->"+editTextroomno.getText().toString()+"\n"+
//                "ph no-->"+editTextmobileno.getText().toString()+"\n"+
//                "email-->"+editTextemail.getText().toString());
        document.addSubject("Feedback");
        document.addCreationDate();
    }
    //// add for statstics
    private void addMetaDataForStatstics(Document document)
    {
        // Open Document for Writting into document
        document.open();
        document.addTitle("PEOPLE TREE CANTEEN STATSTICS");
//        document.addSubject("Name -->"+editTextname.getText().toString()+"\n"+
//                "Room no-->"+editTextroomno.getText().toString()+"\n"+
//                "ph no-->"+editTextmobileno.getText().toString()+"\n"+
//                "email-->"+editTextemail.getText().toString());
        document.addSubject("STATSTICS");
        document.addCreationDate();
    }
    public void addTitlePage(Document document) throws DocumentException
    {
// Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                | Font.UNDERLINE, BaseColor.GRAY);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

// Start New Paragraph
        Paragraph prHead = new Paragraph();
// Set Font in this Paragraph
        prHead.setFont(titleFont);
// Add item into Paragraph
        prHead.add("\nPEOPLE CAFE FEEDBACK FORM\n");

// Create Table into Document with 1 Row
        PdfPTable myTable = new PdfPTable(1);
// 100.0f mean width of table is same as Document size
        myTable.setWidthPercentage(100.0f);

// Create New Cell into Table
        PdfPCell myCell = new PdfPCell(new Paragraph(""));
        myCell.setBorder(Rectangle.BOTTOM);

// Add Cell into Table
        myTable.addCell(myCell);

        prHead.setFont(catFont);
        prHead.setAlignment(Element.ALIGN_CENTER);

// Add all above details into Document
        document.add(prHead);
//        document.add(myTable);
//        document.add(myTable);

// Now Start another New Paragraph
        Paragraph prPersinalInfo = new Paragraph();
        prPersinalInfo.setFont(smallBold);
        prPersinalInfo.add("\nName -->\n"+editTextname.getText().toString()+"\n"+
                        "Room no -->\n"+editTextroomno.getText().toString()+"\n"+
                        "ph no -->\n"+editTextmobileno.getText().toString()+"\n"+
                        "email -->\n"+editTextemail.getText().toString()+"\n");
        String Datetime;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        Datetime = dateformat.format(c.getTime());
        System.out.println(Datetime);
        prPersinalInfo.add(Datetime);

        prPersinalInfo.setAlignment(Element.ALIGN_CENTER);

        document.add(prPersinalInfo);
//        document.add(myTable);
//        document.add(myTable);

        Paragraph prProfile = new Paragraph();
        prProfile.setFont(smallBold);
        prProfile.add("\n \n Results: \n ");
        prProfile.setFont(normal);
        prProfile.add("1. The hospital food has been as good as i expected."+"\n"
                +tempQ1Ans+"\n"
                +"2. The staff who delever my meals are neat and clean."+"\n"
                +tempQ2Ans+"\n"
                +"3. The meals taste good."+"\n"
                +tempQ3Ans+"\n"
                +"4. The hot drinks are just the right temperature."+"\n"
                +tempQ4Ans+"\n"
                +"5. The hot food are just the right temperature."+"\n"
                +tempQ5Ans+"\n"
                +"6. I recived enough food."+"\n"
                +tempQ6Ans+"\n"
                +"7. Overall, how would you rate your satisfaction with the food service ?"+"\n"
                +tempQ7Ans+"\n");

        prProfile.add("\n");

        prProfile.add(tempComment);

        prProfile.setFont(smallBold);
        document.add(prProfile);

// Create new Page in PDF
        document.newPage();
    }
    ////////////////////////////////////

    public void addTitlePageStastics(Document document) throws DocumentException
    {
// Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                | Font.UNDERLINE, BaseColor.GRAY);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

// Start New Paragraph
        Paragraph prHead = new Paragraph();
// Set Font in this Paragraph
        prHead.setFont(titleFont);
// Add item into Paragraph
        prHead.add("\nPEOPLE CAFE FEEDBACK RESULT\n");

// Create Table into Document with 1 Row
        PdfPTable myTable = new PdfPTable(1);
// 100.0f mean width of table is same as Document size
        myTable.setWidthPercentage(100.0f);

// Create New Cell into Table
        PdfPCell myCell = new PdfPCell(new Paragraph(""));
        myCell.setBorder(Rectangle.BOTTOM);

// Add Cell into Table
        myTable.addCell(myCell);

        prHead.setFont(catFont);
        prHead.setAlignment(Element.ALIGN_CENTER);

// Add all above details into Document
        document.add(prHead);
//        document.add(myTable);
//        document.add(myTable);

// Now Start another New Paragraph
        Paragraph prPersinalInfo = new Paragraph();
        prPersinalInfo.setFont(smallBold);
        prPersinalInfo.add("Total feedback's collected -->" + dbLite.getCount().get(0).get_count());


        prPersinalInfo.setAlignment(Element.ALIGN_CENTER);

        document.add(prPersinalInfo);
//        document.add(myTable);
//        document.add(myTable);

        Paragraph prProfile = new Paragraph();
        prProfile.setFont(smallBold);
        prProfile.add("\n \n Results: \n ");
        prProfile.setFont(normal);
        prProfile.add("1. The hospital food has been as good as i expected."+"\n");
        prProfile.add("Always \n" +
                "" +
                ""+(dbLite.getQ1().get(0).get_always()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add(" "+"\nusualy\n" +
                "" +
                ""+(dbLite.getQ1().get(0).get_usualy()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nsometimes\n" +
                "" +
                ""+(dbLite.getQ1().get(0).get_sometimes()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nnever\n" +
                "" +
                ""+(dbLite.getQ1().get(0).get_never()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\n2. The staff who delever my meals are neat and clean."+"\n");
        prProfile.add("Always\n" +
                "" +
                ""+(dbLite.getQ2().get(0).get_always()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nusualy\n" +
                "" +
                ""+(dbLite.getQ2().get(0).get_usualy()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nsometimes\n" +
                "" +
                ""+(dbLite.getQ2().get(0).get_sometimes()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nnever\n" +
                "" +
                ""+(dbLite.getQ2().get(0).get_never()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\n");
        prProfile.add("\n3. The meals taste good."+"\n");
        prProfile.add("Always\n" +
                "\n"+(dbLite.getQ3().get(0).get_always()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nusualy\n" +
                "" +
                ""+(dbLite.getQ3().get(0).get_usualy()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nsometimes\n" +
                "" +
                ""+(dbLite.getQ3().get(0).get_sometimes()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nnever\n" +
                "" +
                ""+(dbLite.getQ3().get(0).get_never()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\n");
        prProfile.add("\n4. The hot drinks are just the right temperature."+"\n");
        prProfile.add("Always\n" +
                "" +
                ""+(dbLite.getQ4().get(0).get_always()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nusualy\n" +
                "" +
                ""+(dbLite.getQ4().get(0).get_usualy()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nsometimes\n" +
                "" +
                ""+(dbLite.getQ4().get(0).get_sometimes()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nnever\n" +
                "" +
                ""+(dbLite.getQ4().get(0).get_never()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\n");
        prProfile.add("\n5. The hot food are just the right temperature."+"\n");
        prProfile.add("Always\n" +
                "" +
                ""+(dbLite.getQ5().get(0).get_always()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nusualy\n" +
                "" +
                ""+(dbLite.getQ5().get(0).get_usualy()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nsometimes\n" +
                "" +
                ""+(dbLite.getQ5().get(0).get_sometimes()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nnever\n" +
                "" +
                ""+(dbLite.getQ5().get(0).get_never()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\n");
        prProfile.add("\n6. I recived enough food."+"\n");
        prProfile.add("Always\n" +
                "" +
                ""+(dbLite.getQ6().get(0).get_always()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nusualy\n" +
                "" +
                ""+(dbLite.getQ6().get(0).get_usualy()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nsometimes\n" +
                "" +
                ""+(dbLite.getQ6().get(0).get_sometimes()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nnever\n" +
                "" +
                ""+(dbLite.getQ6().get(0).get_never()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\n");
        prProfile.add("\n7. Overall, how would you rate your satisfaction with the food service."+"\n");
        prProfile.add("Excellent\n" +
                "" +
                ""+(dbLite.getQ7().get(0).get_excellent()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nVery Good\n" +
                "" +
                ""+(dbLite.getQ7().get(0).get_veryGood()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nGood \n" +
                "" +
                ""+(dbLite.getQ7().get(0).get_good()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nAverage \n" +
                "" +
                ""+(dbLite.getQ7().get(0).get_average()*100)/dbLite.getCount().get(0).get_count());
        prProfile.add("\nPoor \n" +
                "" +
                ""+(dbLite.getQ7().get(0).get_poor()*100)/dbLite.getCount().get(0).get_count());


        prProfile.setFont(smallBold);
        document.add(prProfile);

// Create new Page in PDF
        document.newPage();
    }



    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateName() {
        if (editTextname.getText().toString().trim().isEmpty()) {
            inputLayoutname.setError("Please enter your name..");
            requestFocus(editTextname);
            return false;
        } else {
            inputLayoutname.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateroomno() {
        if (editTextroomno.getText().toString().trim().isEmpty()) {
            inputLayoutroomno.setError("Please enter your room no..");
            requestFocus(editTextroomno);
            return false;
        } else {
            inputLayoutroomno.setErrorEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_roomno:
                    validateroomno();
                    break;
            }
        }
    }
}
