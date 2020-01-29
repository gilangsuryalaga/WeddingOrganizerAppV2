package com.example.weddingorganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weddingorganizer.Model.AdminOrders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportPDFActivity extends AppCompatActivity {
    public TextView order_name, order_phone, order_address, order_date, order_total;
    public Button printPDF;
    private String userId = "";
    private LinearLayout con_pdf;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_pdf);

        order_name = (TextView) findViewById(R.id.order_name);
        order_phone = (TextView) findViewById(R.id.order_phone);
        order_address = (TextView) findViewById(R.id.order_address);
        order_date = (TextView) findViewById(R.id.order_date);
        printPDF = (Button) findViewById(R.id.print_pdf);
        con_pdf = (LinearLayout) findViewById(R.id.con_pdf);
        order_total = (TextView) findViewById(R.id.order_total);


        userId = getIntent().getStringExtra("uid");
        getOrderDetail(userId);

        printPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("size", " " + con_pdf.getWidth() + "  " + con_pdf.getWidth());
                bitmap = loadBitmapFromView(con_pdf, con_pdf.getWidth(), con_pdf.getHeight());
                createPdf();
            }
        });

    }
    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private void getOrderDetail(String userId) {
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AdminOrders order = dataSnapshot.getValue(AdminOrders.class);
                    order_name.setText(order.getName());
                    order_address.setText(order.getAddress() + " ," + order.getCity());
                    order_date.setText(""+order.getDate());
                    order_phone.setText(""+order.getPhone());
                    order_total.setText("Total : "+order.getTotalAmount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createPdf() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        String targetPdf = "/sdcard/invoice.pdf";
        File filePath;
        filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(this, "Export to PDF is Success!!!", Toast.LENGTH_SHORT).show();

        openGeneratedPDF();

    }

    private void openGeneratedPDF() {
        File file = new File("/sdcard/invoice.pdf");
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ExportPDFActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

}
