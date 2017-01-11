package yaran.com.lecturenoteskeeper;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static int width, height;
    public static Context context;
    ImageView imageField;
    MaterialEditText titleField, dateField, timeField, subjectField, commentField, homeworkDateField, homeworkTimeField, otherCommentField;
    SwitchCompat needNotification;

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.requestLayout();
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        final Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.loginButtonColor));
        }
        //ask for permission. next time we should move it to another code block
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        final TextView userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.UserName);
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.getText().equals(getResources().getString(R.string.drawer_login))) {

                } else {
                    //exit form google account
                }
            }
        });


        //fab code. should move to its own class
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.calendar);
        final Calendar cal = Calendar.getInstance();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                final RelativeLayout addNoteLayout = (RelativeLayout) getLayoutInflater()
                        .inflate(R.layout.add_note, null);
                builder.setView(addNoteLayout);
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.dialog_enter), null);
                builder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.show();

                imageField = (ImageView) addNoteLayout.findViewById(R.id.image_field);
                imageField.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, 1);
                    }
                });

                titleField = (MaterialEditText) addNoteLayout.findViewById(R.id.title_field);
                dateField = (MaterialEditText) addNoteLayout.findViewById(R.id.date_field);
                dateField.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                final AlertDialog.Builder dateBuilder =
                                        new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                                final DatePicker datePicker = new DatePicker(MainActivity.this);
                                datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                                dateBuilder.setView(datePicker);
                                dateBuilder.setCancelable(false);
                                dateBuilder.setPositiveButton(getResources().getString(R.string.dialog_enter), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (datePicker.getMonth() < 9)
                                            dateField.setText(datePicker.getDayOfMonth() + ".0" + (datePicker.getMonth() + 1) + "." + datePicker.getYear());
                                        else
                                            dateField.setText(datePicker.getDayOfMonth() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getYear());
                                        dialog.cancel();
                                    }
                                });
                                dateBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                                dateBuilder.show();
                                break;
                        }
                        return true;
                    }
                });
                timeField = (MaterialEditText) addNoteLayout.findViewById(R.id.time_field);
                timeField.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                final AlertDialog.Builder timeBuilder =
                                        new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                                final TimePicker timePicker = new TimePicker(MainActivity.this);
                                timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
                                timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
                                timeBuilder.setView(timePicker);
                                timeBuilder.setCancelable(false);
                                timeBuilder.setPositiveButton(getResources().getString(R.string.dialog_enter), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (timePicker.getCurrentHour() < 10 & timePicker.getCurrentMinute() < 10)
                                            timeField.setText("0" + timePicker.getCurrentHour() + ":0" + timePicker.getCurrentMinute());
                                        else if (timePicker.getCurrentHour() < 10)
                                            timeField.setText("0" + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                                        else
                                            timeField.setText(timePicker.getCurrentHour() + ":0" + timePicker.getCurrentMinute());
                                        dialog.cancel();
                                    }
                                });
                                timeBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                                timeBuilder.show();
                                break;
                        }
                        return true;
                    }
                });

                Spinner typeSpinner = (Spinner) addNoteLayout.findViewById(R.id.spinner);
                typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0) {
                            subjectField = (MaterialEditText) addNoteLayout.findViewById(R.id.subject_field);
                            subjectField.setVisibility(View.VISIBLE);
                            commentField = (MaterialEditText) addNoteLayout.findViewById(R.id.comment_field);
                            commentField.setVisibility(View.VISIBLE);
                            try {
                                homeworkDateField.setVisibility(View.INVISIBLE);
                                homeworkTimeField.setVisibility(View.INVISIBLE);
                                needNotification.setVisibility(View.INVISIBLE);
                                otherCommentField.setVisibility(View.INVISIBLE);
                            } catch (java.lang.NullPointerException g) {
                            }
                        } else if (i == 1) {
                            try {
                                subjectField.setVisibility(View.INVISIBLE);
                                commentField.setVisibility(View.INVISIBLE);
                                otherCommentField.setVisibility(View.INVISIBLE);
                            } catch (java.lang.NullPointerException g) {
                            }
                            homeworkDateField = (MaterialEditText) addNoteLayout.findViewById(R.id.homework_date_field);
                            homeworkDateField.setVisibility(View.VISIBLE);
                            homeworkDateField.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    switch (motionEvent.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                            final AlertDialog.Builder dateBuilder =
                                                    new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                                            final DatePicker datePicker = new DatePicker(MainActivity.this);
                                            datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                                            dateBuilder.setView(datePicker);
                                            dateBuilder.setCancelable(false);
                                            dateBuilder.setPositiveButton(getResources().getString(R.string.dialog_enter), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    if (datePicker.getMonth() < 9)
                                                        homeworkDateField.setText(datePicker.getDayOfMonth() + ".0" + (datePicker.getMonth() + 1) + "." + datePicker.getYear());
                                                    else
                                                        homeworkDateField.setText(datePicker.getDayOfMonth() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getYear());
                                                    dialog.cancel();
                                                }
                                            });
                                            dateBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                            dateBuilder.show();
                                            break;
                                    }
                                    return true;
                                }
                            });

                            homeworkTimeField = (MaterialEditText) addNoteLayout.findViewById(R.id.homework_time_field);
                            homeworkTimeField.setVisibility(View.VISIBLE);
                            homeworkTimeField.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    switch (motionEvent.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                            final AlertDialog.Builder timeBuilder =
                                                    new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                                            final TimePicker timePicker = new TimePicker(MainActivity.this);
                                            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
                                            timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
                                            timeBuilder.setView(timePicker);
                                            timeBuilder.setCancelable(false);
                                            timeBuilder.setPositiveButton(getResources().getString(R.string.dialog_enter), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    if (timePicker.getCurrentHour() < 10 & timePicker.getCurrentMinute() < 10)
                                                        homeworkTimeField.setText("0" + timePicker.getCurrentHour() + ":0" + timePicker.getCurrentMinute());
                                                    else if (timePicker.getCurrentHour() < 10)
                                                        homeworkTimeField.setText("0" + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                                                    else
                                                        homeworkTimeField.setText(timePicker.getCurrentHour() + ":0" + timePicker.getCurrentMinute());
                                                    dialog.cancel();
                                                }
                                            });
                                            timeBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                            timeBuilder.show();
                                            break;
                                    }
                                    return true;
                                }
                            });

                            needNotification = (SwitchCompat) addNoteLayout.findViewById(R.id.need_notification);
                            needNotification.setVisibility(View.VISIBLE);
                        } else if (i == 2) {
                            try {
                                subjectField.setVisibility(View.INVISIBLE);
                                commentField.setVisibility(View.INVISIBLE);
                                homeworkDateField.setVisibility(View.INVISIBLE);
                                homeworkTimeField.setVisibility(View.INVISIBLE);
                                needNotification.setVisibility(View.INVISIBLE);
                            } catch (java.lang.NullPointerException g) {
                            }
                            otherCommentField = (MaterialEditText) addNoteLayout.findViewById(R.id.other_comment_field);
                            otherCommentField.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.drawerSettings:
                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        try {
            drawer.closeDrawer(GravityCompat.START);
        } catch (java.lang.NullPointerException gf) {
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.d("picturePath = ", picturePath);
            imageField.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            File file = new File(picturePath);
            if (file.exists()) {
                Date lastModDate = new Date(file.lastModified());
                Calendar cal = Calendar.getInstance();
                cal.setTime(lastModDate);
                if (dateField.getText().length() < 1)
                    dateField.setText(cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR));
                if (timeField.getText().length() < 1)
                    timeField.setText(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
                if (titleField.getText().length() < 1)
                    titleField.setText(file.getName());
            }
        }
    }
}
