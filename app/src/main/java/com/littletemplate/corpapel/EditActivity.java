package com.littletemplate.corpapel;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.littletemplate.corpapel.app.BaseActivity;

public class EditActivity extends BaseActivity implements AdapterView.OnItemSelectedListener  {
    Spinner spinnerDistrito;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Button yourButton = (Button) findViewById(R.id.button2);

        yourButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              startActivity(new Intent(EditActivity.this, PrincipalActivity.class));
                                          }
                                      }
        );


        Spinner spinnerDepartamento = (Spinner) findViewById(R.id.spinner_departamento);
        ArrayAdapter<CharSequence> adapterDEPARTAMENTO = ArrayAdapter.createFromResource(
                this, R.array.DEPARTAMENTO_array, R.layout.spinner_item);
        adapterDEPARTAMENTO.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerDepartamento.setAdapter(adapterDEPARTAMENTO);



        Spinner spinnerProvincia = (Spinner) findViewById(R.id.spinner_provincia);
        ArrayAdapter<CharSequence> adapterPROVINCIA = ArrayAdapter.createFromResource(
                this, R.array.PROVINCIA_array, R.layout.spinner_item);
        adapterPROVINCIA.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerProvincia.setAdapter(adapterPROVINCIA);
        spinnerProvincia.setOnItemSelectedListener(this);




        spinnerDistrito =  (Spinner) findViewById(R.id.spinner_distrito);
        ArrayAdapter<CharSequence> adapterDISTRITO = ArrayAdapter.createFromResource(
                this, R.array.DISTRITO_array, R.layout.spinner_item);
        adapterDISTRITO.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerDistrito.setAdapter(adapterDISTRITO);


    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);

        if (position == 1) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.BARRANCA_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);

        }
        else
        if (position == 2) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.CAJATAMBO_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);
        }
        else
        if (position == 3) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.CANTA_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);
        }
        else
        if (position == 4) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.CAÑETE_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);
        }
        else
        if (position == 5) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.HUARAL_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);
        }
        else
        if (position == 6) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.HUAROCHIRI_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);
        }
        else
        if (position == 7) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.HUAURA_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);
        }
        else
        if (position == 8) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.LIMA_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);
        }
        else
        if (position == 9) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.OYON_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);
        }
        else
        if (position == 10) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.YUAYOS_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);
        }
        else {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.DISTRITO_array,
                    R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDistrito.setAdapter(adapter);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
