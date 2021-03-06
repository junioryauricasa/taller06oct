package com.example.taller06oct;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditEmpleadosActivity extends Activity {

	TextView lblcedula;
	EditText txtNombre;
	EditText txtApellido;
	EditText txtSueldo;
	
	Button btnSave;
	Button btnDelete;

	String cedula;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single Empleado url - Reemplaza la IP de tu equipo o la direccion de tu servidor 
	// Si tu servidor es tu PC colocar IP Ej: "http://127.97.99.200/taller06oct/..", no colocar "http://localhost/taller06oct/.."
	private static final String url_detalles_empleado = "http://AquiTuServidor/taller06oct/datosempleado.php";

	// url to update Empleado - Reemplaza la IP de tu equipo o la direccion de tu servidor 
	private static final String url_actualizar_empleado = "http://AquiTuServidor/taller06oct/actualizarempleado.php";
	
	// url to delete Empleado - Reemplaza la IP de tu equipo o la direccion de tu servidor 
	private static final String url_borrar_empleado = "http://AquiTuServidor/taller06oct/borrarempleado.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_EMPLEADOS = "empleado";
	private static final String TAG_CEDULA = "cedula";
	private static final String TAG_NOMBRE = "nombre";
	private static final String TAG_APELLIDO = "apellido";
	private static final String TAG_SUELDO = "sueldo";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_empleado);

		// save button
		btnSave = (Button) findViewById(R.id.btnSave);
		btnDelete = (Button) findViewById(R.id.btnDelete);

		// getting Empleado details from intent
		Intent i = getIntent();
		
		// getting Empleado id (pid) from intent
		cedula = i.getStringExtra(TAG_CEDULA);

		// Getting complete Empleado details in background thread
		new GetEmpleadoDetails().execute();

		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update Empleado
				new SaveEmpleadoDetails().execute();
			}
		});

		// Delete button click event
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleting Empleado in background thread
				new DeleteEmpleado().execute();
			}
		});

	}

	/**
	 * Background Async Task to Get complete Empleado details
	 * */
	class GetEmpleadoDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditEmpleadosActivity.this);
			pDialog.setMessage("Loading Empleado details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting Empleado details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("cedula", cedula));

						// getting Empleado details by making HTTP request
						// Note that Empleado details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_detalles_empleado, "GET", params);

						// check your log for json response
						Log.d("Single Empleado Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received Empleado details
							JSONArray EmpleadoObj = json
									.getJSONArray(TAG_EMPLEADOS); // JSON Array
							
							// get first Empleado object from JSON Array
							JSONObject Empleado = EmpleadoObj.getJSONObject(0);

							// Empleado with this pid found
							// Edit Text
							lblcedula = (TextView) findViewById(R.id.lblcedula);
							txtNombre = (EditText) findViewById(R.id.inputNombre);
							txtApellido = (EditText) findViewById(R.id.inputApellido);
							txtSueldo = (EditText) findViewById(R.id.inputSueldo);

							// display Empleado data in EditText
							lblcedula.setText(Empleado.getString(TAG_CEDULA));
							txtNombre.setText(Empleado.getString(TAG_NOMBRE));
							txtApellido.setText(Empleado.getString(TAG_APELLIDO));
							txtSueldo.setText(Empleado.getString(TAG_SUELDO));

						}else{
							// Empleado with pid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}

	/**
	 * Background Async Task to  Save Empleado Details
	 * */
	class SaveEmpleadoDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditEmpleadosActivity.this);
			pDialog.setMessage("Actualizando Empleado ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving Empleado
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			
			String nombre = txtNombre.getText().toString();
			String apellido = txtApellido.getText().toString();
			String sueldo = txtSueldo.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_CEDULA, cedula));
			params.add(new BasicNameValuePair(TAG_NOMBRE, nombre));
			params.add(new BasicNameValuePair(TAG_APELLIDO, apellido));
			params.add(new BasicNameValuePair(TAG_SUELDO, sueldo));

			// sending modified data through http request
			// Notice that update Empleado url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_actualizar_empleado,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about Empleado update
					setResult(100, i);
					finish();
				} else {
					// failed to update Empleado
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once Empleado uupdated
			pDialog.dismiss();
		}
	}

	/*****************************************************************
	 * Background Async Task to Delete Empleado
	 * */
	class DeleteEmpleado extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditEmpleadosActivity.this);
			pDialog.setMessage("Borrando Empleado...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting Empleado
		 * */
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("cedula", cedula));

				// getting Empleado details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_borrar_empleado, "POST", params);

				// check your log for json response
				Log.d("Borrando Empleado", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// Empleado successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify about Empleado deletion
					setResult(100, i);
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once Empleado deleted
			pDialog.dismiss();

		}

	}
}
