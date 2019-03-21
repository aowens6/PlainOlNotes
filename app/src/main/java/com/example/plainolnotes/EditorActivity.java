package com.example.plainolnotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URI;

public class EditorActivity extends AppCompatActivity {

  private String action;
  private EditText editor;
  private Button deleteButton;
  private String noteFilter;
  private String oldText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_editor);

    editor = findViewById(R.id.editText);
    Intent intent = getIntent();
    Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
    if (uri == null) {
      action = Intent.ACTION_INSERT;
      setTitle(R.string.new_note);
      deleteButton = findViewById(R.id.button2);
      deleteButton.setVisibility(View.GONE);
    }else{
      action = Intent.ACTION_EDIT;
      noteFilter=DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
      Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS,
              noteFilter,null, null);
      cursor.moveToFirst();
      oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
      editor.setText(oldText);
      editor.requestFocus();
    }
  }

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//
//    if(action.equals(Intent.ACTION_EDIT)){
//
//    }
//
//  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (item.getItemId()){
      case android.R.id.home:
        finishedEditing();
        break;
    }
    return true;
  }

  private void finishedEditing(){
    String newText = editor.getText().toString().trim();

    switch (action){
      case Intent.ACTION_INSERT:
        if (newText.length() == 0) {
          setResult(RESULT_CANCELED);
        }else {
          insertNote(newText);
        }
        break;
      case Intent.ACTION_EDIT:
        if(newText.length() == 0){
          deleteBlankNote();
        }else if(oldText.equals(newText)){
          setResult(RESULT_CANCELED);
        }else{
          updateNote(newText);
        }
    }
    finish();
  }

  public void deleteBlankNote() {
    getContentResolver().delete(NotesProvider.CONTENT_URI,
            noteFilter,null);
    Toast.makeText(this, R.string.note_deleted,Toast.LENGTH_SHORT).show();
    setResult(RESULT_OK);
    finish();
  }

  public void deleteNote(View view) {
    getContentResolver().delete(NotesProvider.CONTENT_URI,
            noteFilter,null);
    Toast.makeText(this, R.string.note_deleted,Toast.LENGTH_SHORT).show();
    setResult(RESULT_OK);
    finish();
  }

  private void updateNote(String noteText) {
    ContentValues values = new ContentValues();
    values.put(DBOpenHelper.NOTE_TEXT, noteText);
    getContentResolver().update(NotesProvider.CONTENT_URI,
            values,noteFilter,null);
    Toast.makeText(this, R.string.note_updated,
            Toast.LENGTH_SHORT).show();
    setResult(RESULT_OK);
  }

  private void insertNote(String noteText) {
    ContentValues values = new ContentValues();
    values.put(DBOpenHelper.NOTE_TEXT, noteText);
    getContentResolver().insert(NotesProvider.CONTENT_URI, values);
    setResult(RESULT_OK);
  }

  @Override
  public void onBackPressed() {
    finishedEditing();
  }
}
