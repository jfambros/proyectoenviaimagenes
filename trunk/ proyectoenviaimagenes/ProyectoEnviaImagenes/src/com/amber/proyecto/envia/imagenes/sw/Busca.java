package com.amber.proyecto.envia.imagenes.sw;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;

public class Busca extends Activity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busca);
	}
	
	private void fillData() {
	    Cursor c = mdbHelper.fetchAllNotes();
	    startManagingCursor(c);

	    String[] from = new String[] { "nombreCategoria", "idCategoria" };
	    int[] to = new int[] { R.id.text1, R.id.CheckBox1 };

	    // Now create an array adapter and set it to display using our row
	    SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.notes_row, c, from, to);

	    notes.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
	      public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
	        int nCheckedIndex = cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_CHECK);
	        if (columnIndex == nCheckedIndex) {
	          CheckBox cb = (CheckBox) view;
	          boolean bChecked = (cursor.getInt(nCheckedIndex) != 0);
	          cb.setChecked(bChecked);
	          return true;
	        }
	        return false;
	      }
	    });
	    setListAdapter(notes);

	  }

}
