package com.regionaldeals.de;

/**
 * Created by Umi on 28.08.2017.
 */

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.Utils.SharedPreferenceUtils;
import com.regionaldeals.de.entities.CitiesObject;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.regionaldeals.de.Constants.CITIES_KEY;
import static com.regionaldeals.de.Constants.LOCATION_KEY;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    private JSONParser jsonParser = new JSONParser();
    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 99;
    private final String URL_Cities = "/mobile/api/device/citieslist";
    private JSONArray data;
    private String[] COUNTRIES;
    private String message = "";
    private StringBuilder sb;
    private List<CitiesObject> city = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferenceUtils.getInstance(this);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        createDialogue();

    }
    public void createDialogue() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SplashActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialogue_checkbox, null);
        CheckBox mCheckBox = mView.findViewById(R.id.checkBox);
        TextView tv = mView.findViewById(R.id.tvDiaogue);
        tv.setText(Html.fromHtml(getString(R.string.datahtml)));
        mBuilder.setTitle("Datenschutzerklärung");
        mBuilder.setView(mView);
        mBuilder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                getLocation();
            }
        });

        final AlertDialog mDialog = mBuilder.create();
        mDialog.show();

        mDialog.setCanceledOnTouchOutside(false);

        ((AlertDialog) mDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    ((AlertDialog) mDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(true);
                }else{
                    ((AlertDialog) mDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(false);
                }
            }
        });
    }

    class loadCities extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            sb = new StringBuilder();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(getString(R.string.apiUrl) + URL_Cities, "GET",
                    params);
            Log.d("JSON: ", "> " + json);
            try {
                JSONObject jsonObj = new JSONObject(json);
                data = jsonObj.getJSONArray("data");
                message = jsonObj.getString("message");
                city.clear();
                COUNTRIES = new String[data.length()];
                for (int i = 0; i < data.length(); i++) {
                    JSONObject result = (JSONObject) data.get(i);
                    CitiesObject c = new CitiesObject();
                    c.setCityName(result.getString("cityName"));
                    c.setCountryCode(result.getString("countryCode"));
                    c.setId(result.getInt("id"));
                    COUNTRIES[i] = result.getString("cityName");
                    sb.append(COUNTRIES[i]).append(",");
                    if (!result.isNull("cityLat") && !result.isNull("cityLong")) {
                        c.setCityLat(result.getDouble("cityLat"));
                        c.setCityLong(result.getDouble("cityLong"));
                    }
                    city.add(c);
                }
                Arrays.sort(COUNTRIES);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            if (message.equals("CITIES_LIST_OK") && COUNTRIES.length > 1) {
                //all good
            } else {
                Arrays.sort(DEFAULTCOUNTRIES);
                for (int i = 0; i < DEFAULTCOUNTRIES.length; i++) {
                    sb.append(DEFAULTCOUNTRIES[i]).append(",");
                }
            }

            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE).edit();
            editor.putString("citiesString", sb.toString());
            Gson gson = new Gson();
            String json = gson.toJson(city);
            editor.putString("citiesObject", json);
            editor.commit();

            runOnUiThread(new Runnable() {
                public void run() {
                    Intent startActivityIntent = new Intent(SplashActivity.this, LocationManual.class);
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            });
        }
    }

    public void getLocation() {
        int status = getPackageManager().checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                getPackageName());
        if (status == PackageManager.PERMISSION_GRANTED) {
            nextFlow();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COURSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    getLocation();
                } else {
                    Toast.makeText(this, "Cannot get user location", Toast.LENGTH_SHORT).show();
                    nextFlow();
                }
                return;
            }
        }
    }


    private void nextFlow() {

        String restoredText = SharedPreferenceUtils.getInstance(this).getStringValue(LOCATION_KEY, null);
        String restoredCities = SharedPreferenceUtils.getInstance(this).getStringValue(CITIES_KEY, null);

        if (restoredText != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
                    if (getIntent().hasExtra("notificationBody")) {
                        String body = getIntent().getStringExtra("notificationBody");
                        startActivityIntent.putExtra("notificationBody", body);
                    } else if (getIntent().hasExtra("dealids")) {     //should be redirect = true
                        String body = getIntent().getStringExtra("dealids");
                        startActivityIntent.putExtra("notificationBody", body);
                    } else if (getIntent().hasExtra("notificationGut")) {
                        String body = getIntent().getStringExtra("notificationGut");
                        startActivityIntent.putExtra("notificationGut", body);
                    } else if (getIntent().hasExtra("gutscheinid")) {
                        String body = getIntent().getStringExtra("notificationGut");
                        startActivityIntent.putExtra("notificationGut", body);
                    }
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else if (restoredCities != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startActivityIntent = new Intent(SplashActivity.this, LocationManual.class);
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new SplashActivity.loadCities().execute();
                }
            }, SPLASH_DISPLAY_LENGTH - 500);
        }
    }

    // an array with countries to display in the list
    private static String[] DEFAULTCOUNTRIES = new String[]
            {
                    "Aachen",
                    "Augsburg",
                    "Bergisch",
                    "Berlin",
                    "Bielefeld",
                    "Bochum",
                    "Bonn",
                    "Bottrop",
                    "Braunschweig",
                    "Bremen",
                    "Bremerhaven",
                    "Chemnitz",
                    "Cottbus",
                    "Darmstadt",
                    "Dessau-Roßlau",
                    "Dortmund",
                    "Dresden",
                    "Duisburg",
                    "Düren",
                    "Düsseldorf",
                    "Erfurt",
                    "Erlangen",
                    "Essen",
                    "Esslingen",
                    "Flensburg",
                    "Frankfurt",
                    "Freiburg",
                    "Fürth",
                    "Gelsenkirchen",
                    "Gera",
                    "Gladbach",
                    "Göttingen",
                    "Gütersloh",
                    "Hagen",
                    "Halle",
                    "Hamburg",
                    "Hamm",
                    "Hanau",
                    "Hannover",
                    "Heidelberg",
                    "Heilbronn",
                    "Herne",
                    "Hildesheim",
                    "Ingolstadt",
                    "Iserlohn",
                    "Jena",
                    "Kaiserslautern",
                    "Karlsruhe",
                    "Kassel",
                    "Kiel",
                    "Koblenz",
                    "Krefeld",
                    "Köln",
                    "Leipzig",
                    "Leverkusen",
                    "Ludwigsburg",
                    "Ludwigshafen",
                    "Lübeck",
                    "Lünen",
                    "Magdeburg",
                    "Mainz",
                    "Mannheim",
                    "Marl",
                    "Minden",
                    "Moers",
                    "Mönchengladbach",
                    "Mülheim",
                    "München",
                    "Münster",
                    "Neuss",
                    "Nürnberg",
                    "Oberhausen",
                    "Offenbach",
                    "Oldenburg",
                    "Osnabrück",
                    "Paderborn",
                    "Pforzheim",
                    "Potsdam",
                    "Ratingen",
                    "Recklinghausen",
                    "Regensburg",
                    "Remscheid",
                    "Reutlingen",
                    "Rostock",
                    "Saarbrücken",
                    "Salzgitter",
                    "Schwerin",
                    "Siegen",
                    "Solingen",
                    "Stuttgart",
                    "Trier",
                    "Tübingen",
                    "Ulm",
                    "Velbert",
                    "Villingen-Schwenn.",
                    "Wiesbaden",
                    "Witten",
                    "Wolfsburg",
                    "Wuppertal",
                    "Würzburg",
                    "Zwickau"
            };


    String dataschnutz = "" +
            "A. Allgemeine Hinweise\n" +
            "Geltungsbereich der Datenschutzerklärung\n" +
            "Diese Datenschutzerklärung gilt für den Besuch und die Nutzung der Website http://www.regionaldeals.de sowie für die Applikation Regionaldeals.\n" +
            "Verantwortlicher\n" +
            "Wir, Regionaldeals RD e.K. , Inh. Ferit Baycan, Marienstraße 11, 52146 Würselen, sind der Verantwortliche im Sinne der EU-DSGVO – also das Einzelunternehmen (e.K.), das über die Zwecke und Mittel der Verarbeitung von personenbezogenen Daten entscheidet. Unsere Kontaktdaten lauten: Marienstraße 11, 52146 Würselen, Tel.: +49 (0) [XXX], E-Mail: [XXX]\n" +
            "Ansprechpartner für alle Belange des Datenschutzes in unserem Hause ist: Ferit Baycan\n" +
            "Weiterverarbeitung für einen anderen Zweck\n" +
            "Die Zwecke, für die wir Ihre personenbezogenen Daten verarbeiten, werden im Abschnitt B. beschrieben. Sofern wir eine Weiterverarbeitung Ihrer personenbezogenen Daten für einen anderen Zweck – also nicht für denjenigen, für den die personenbezogenen Daten ursprünglich erhoben wurden – beabsichtigen, werden wir Sie erneut informieren. \n" +
            "Pflicht zur Bereitstellung / Erforderlichkeit von personenbezogenen Daten für Kontaktaufnahme\n" +
            "Die Bereitstellung der personenbezogenen Daten ist gesetzlich nicht vorgeschrieben. Allerdings sind die für die Bearbeitung einer Anfrage über unser Kontaktformular erforderlichen Daten wie Name, Vorname und E-Mail-Adresse anzugeben. Ohne diese sind wir nicht in der Lage, Ihre Anfrage zu bearbeiten. \n" +
            "B. Verarbeitung Ihrer personenbezogenen Daten \n" +
            "1. Informationen, die Sie uns mitteilen\n" +
            "Pflichtangaben als Kunde und als Nutzer\n" +
            "Um unsere Dienste erbringen zu können, benötigen wir bestimmte Angaben von Ihnen. Wenn Sie sich bei uns als Nutzer registrieren, benötigen wir nur Ihre E-Mail-Adresse,Name und Vorname . Wenn Sie sich als Kunde registrieren, das heißt unsere kostenpflichtigen Dienste in Anspruch nehmen, dann benötigen wir zur Abwicklung des Vertragsverhältnisses idR. Ihren Namen, Vornamen, Geschäftsname, Ihre Adresse, Ihre Telefonnummer, Ihre E-Mail-Adress,und Steuernummer.\n" +
            "Speicherdauer\n" +
            "Wir löschen diese Daten, wenn Sie als Nutzer Ihr Nutzerkonto löschen bzw. wenn Sie Kunde sind, in dem Zeitpunkt, in dem das zwischen uns bestehende Vertragsverhältnis beendet wird. \n" +
            "Rechtsgrundlage\n" +
            "Rechtsgrundlage für diese Verarbeitung personenbezogener Daten ist Art. 6 I b) EU-DSGVO.\n" +
            "\n" +
            "2. Informationen, die wir auf Grund Ihrer Nutzung unserer Website automatisch erhalten\n" +
            "Während Sie unsere Website und die Regionaldeals Applikation nutzen oder besuchen, werden automatisch Daten von Ihnen gesammelt. \n" +
            "Durchführung des Speicherung\n" +
            "Wir sammeln Cookies und  Daten zur Bereitstellung unserer Dienste und zur Auswertung des Nutzerverhaltens sowie zu statistischen Zwecken. Es werden Zugriffsdaten (z. B. Datum und Uhrzeit des Besuches, Referrer, IP-Adresse, Cookie-ID, Standort-Daten, Produkt- und Versionsinformationen des verwendeten Browsers bzw. der verwendeten App, Gerätekennungen oder Gerätedaten) sowie Interaktionsdaten (z. B. angesehene Seiten oder durchgeführte Suchanfragen) verarbeitet. Um Sie als Nutzer während Ihres Besuchs unserer Website identifizieren zu können, setzen wir sogenannte Session-Cookies ein. Diese Session-Cookies werden nach dem Ende der jeweiligen Sitzung automatisch gelöscht. Diese Cookies sind erforderlich, um unsere Website nutzen zu können. \n" +
            "Die Speicherung von Cookies können Sie über Ihre Browser-Einstellungen verhindern.\n" +
            "Zwecke des Speicherns \n" +
            "Gewährleistung der Sicherheit\n" +
            "Das Speichern erfolgt unter anderem zur Gewährleistung und zur Wahrung des berechtigten Interesses des Schutzes der Nutzer, der Sicherheit der Nutzerdaten, als auch unserer Website. Hierzu speichern wir die erhobenen Daten für bis zu 90 Tage in vollständiger Form. Zugriff auf diese Daten haben nur wenige unserer Mitarbeiter mit entsprechenden Zugriffsrechten. Rechtsgrundlage für diese Verarbeitung personenbezogener Daten ist Art. 6 I f) EU-DSGVO.\n" +
            "Bereitstellung unseres Dienstes\n" +
            "Das Speichern und die Analyse des Nutzerverhaltens hilft uns, die Effektivität unseres Dienstes zu überprüfen und zu optimieren sowie Fehler zu beheben. Wir versuchen unsere Produkte und Dienstleistungen stets an die Bedürfnisse der Nutzer anzupassen. \n" +
            "3. Weitergabe Ihrer Daten an Dritte\n" +
            "Wir geben Ihre personenbezogenen Daten grundsätzlich nicht an Dritte weiter, es sei denn dass wir gesetzlich oder aufgrund einer gerichtlichen oder behördlichen Anordnung dazu verpflichtet sind. Wenn wir im Rahmen der Datenverarbeitung mit externen Dienstleistern zusammenarbeiten (z. B. bei der Analyse des Nutzerverhaltens), erfolgt dies in der Regel auf Basis einer sogenannten Auftragsverarbeitung, bei der wir für die Datenverarbeitung verantwortlich bleiben. Wir prüfen jeden dieser Dienstleister vorher auf die von ihm zum Datenschutz und zur Datensicherheit getroffenen Maßnahmen und stellen so die gesetzlich vorgesehenen vertraglichen Regelungen zum Schutz der personenbezogenen Daten sicher.\n" +
            "\n" +
            "C. Ihre Rechte\n" +
            "Ihnen stehen unter anderem gesetzliche Ansprüche auf Auskunft, Berichtigung, Löschung (nach Beantragung), Einschränkung der Verarbeitung und Widerspruch gegen die Verarbeitung sowie ein Recht auf Datenübertragbarkeit zu. Außerdem können Sie eine gegebenenfalls abgegebene Einwilligung in die Verarbeitung jederzeit widerrufen und sich bei einer Aufsichtsbehörde beschweren.\n" +
            "Widerspruchsrecht\n" +
            "Sie haben das Recht, jederzeit gegen die Verarbeitung Sie betreffender personenbezogener Daten, die gemäß Art. 6 Abs. 1 Buchst. f) EU-DSGVO erfolgt, Widerspruch einzulegen. Bitte nutzen Sie für Ihren Widerspruch unser Kontaktformular oder senden Sie uns eine E-Mail.\n" +
            "Auskunftsrecht\n" +
            "Sie haben das Recht, von uns eine Bestätigung darüber zu verlangen, ob wir Sie betreffende personenbezogene Daten verarbeiten. Ist dies der Fall, so haben Sie ein Recht auf Auskunft über diese personenbezogenen Daten.\n" +
            "Berichtigungsrecht\n" +
            "Sie haben das Recht, von uns unverzüglich die Berichtigung Sie betreffender unrichtiger personenbezogener Daten zu verlangen. Unter Berücksichtigung der Zwecke der Verarbeitung haben Sie das Recht, die Vervollständigung unvollständiger personenbezogener Daten – auch mittels einer ergänzenden Erklärung – zu verlangen.\n" +
            "Recht auf Löschung\n" +
            "Sie haben das Recht, von uns zu verlangen, dass Sie betreffende personenbezogenen Daten unverzüglich gelöscht werden, sofern einer der folgenden Gründe zutrifft: Die personenbezogenen Daten sind für die Zwecke, für die sie erhoben oder auf sonstige Weise verarbeitet wurden, nicht mehr notwendig. Sie widerrufen ihre Einwilligung, auf die sich die Verarbeitung gemäß Art. 6 Absatz 1 Buchstabe a oder Art. 9 Absatz 2 Buchstabe a EU-DSGVO stützte, und es fehlt an einer anderweitigen Rechtsgrundlage für die Verarbeitung. Sie legen gemäß Art. 21 Absatz 1 EU-DSGVO Widerspruch gegen die Verarbeitung ein und es liegen keine vorrangigen berechtigten Gründe für die Verarbeitung vor, oder Sie legen gemäß Art. 21 Absatz 2 EU-DSGVO Widerspruch gegen die Verarbeitung ein. Die personenbezogenen Daten wurden unrechtmäßig verarbeitet. Die Löschung der personenbezogenen Daten ist zur Erfüllung einer rechtlichen Verpflichtung nach dem Unionsrecht oder dem Recht der Mitgliedstaaten erforderlich, dem wir unterliegen. Die personenbezogenen Daten wurden in Bezug auf direkt einem Kind angebotene Dienste der Informationsgesellschaft gemäß Art. 8 Absatz 1 EU-DSGVO erhoben. Nach Ihrer Aufforderung sind wir zu einer unverzüglichen Löschung der entsprechenden Daten verpflichtet. Die Rechtmäßigkeit der aufgrund der Einwilligung bis zum Widerruf erfolgten Verarbeitung bleibt unberührt.\n" +
            "Recht auf Einschränkung der Verarbeitung\n" +
            "Sie sind berechtigt, eine Einschränkung bei der Verarbeitung Ihrer personenbezogenen Daten zu verlangen, wenn die Richtigkeit der personenbezogenen Daten von Ihnen bestritten wird, und zwar für die Dauer, die es dem Verantwortlichen ermöglicht, die Richtigkeit der personenbezogenen Daten zu überprüfen. Sofern die Verarbeitung unrechtmäßig ist und Sie die Löschung der personenbezogenen Daten ablehnen und stattdessen die Einschränkung der Nutzung der personenbezogenen Daten von uns verlangen, folgen wir der Aufforderung. Die Einschränkung der Verarbeitung erfolgt auch dann, falls wir Ihre personenbezogenen Daten für Zwecke der Verarbeitung nicht länger benötigen, sie diese aber zur Geltendmachung, Ausübung oder Verteidigung von eigenen Rechtsansprüche benötigen, oder Sie Widerspruch gegen die Verarbeitung gemäß Art. 21 Abs. 1 EU-DSGVO eingelegt haben, solange noch nicht feststeht ob die berechtigten Gründe des Verantwortlichen gegenüber ihren Gründen überwiegen. Sie werden von uns unterrichtet, bevor die Einschränkung aufgehoben wird.\n" +
            "Recht auf Datenübertragbarkeit\n" +
            "Sie haben das Recht, die Sie betreffenden personenbezogenen Daten, die Sie uns bereitgestellt haben, in einem strukturierten, gängigen und maschinenlesbaren Format zu erhalten, und haben das Recht, diese Daten einem anderen Verantwortlichen ohne Behinderung durch uns, denen die personenbezogenen Daten bereitgestellt wurden, zu übermitteln. Voraussetzung ist, dass a) die Verarbeitung auf einer Einwilligung gemäß Art. 6 Abs. 1 Buchst. a) EU-DSGVO oder Art. 9 Abs. 2 Buchst. a) EU-DSGVO oder auf einem Vertrag gemäß Art. 6 Abs. 1 Buchst. b) EU-DSGVO beruht und b) die Verarbeitung mithilfe automatisierter Verfahren erfolgt. Bei der Ausübung des Rechts auf Datenübertragbarkeit haben Sie das Recht zu fordern, dass die personenbezogenen Daten direkt von uns an eine andere verantwortliche Stelle übermittelt werden, soweit dies technisch machbar ist.\n" +
            "Widerrufsrecht bei Einwilligung\n" +
            "Soweit die Verarbeitung auf Ihrer Einwilligung beruht, haben Sie das Recht, die Einwilligung jederzeit zu widerrufen. Die Rechtmäßigkeit der aufgrund der Einwilligung bis zum Widerruf erfolgten Verarbeitung wird dadurch nicht berührt.\n" +
            "Beschwerderecht\n" +
            "Sie haben ein Beschwerderecht bei der für unser Unternehmen zuständigen Aufsichtsbehörde. Die für unser Unternehmen zuständige Aufsichtsbehörde ist: Landesbeauftragte für Datenschutz und Informationsfreiheit Nordrhein-Westfalen, Kavalleriestr. 2-4, 40213 Düsseldorf, Telefon: 0211/38424-0, Fax: 0211/38424-10, E-Mail: poststelle@ldi.nrw.de.\n" +
            "D. Begriffe, die in unserer Datenschutzerklärung vorkommen\n" +
            "EU-DSGVO: Die Verordnung (EU) 2016/679 des Europäischen Parlaments und des Rates vom 27. April 2016 zum Schutz natürlicher Personen bei der Verarbeitung personenbezogener Daten, zum freien Datenverkehr und zur Aufhebung der Richtlinie 95/46/EG (Datenschutz-Grundverordnung).\n" +
            "Personenbezogene Daten: Gemäß Art. 4 Ziffer 1 EU-DSGVO alle Informationen, die sich auf eine identifizierte oder identifizierbare natürliche Person beziehen; als identifizierbar wird eine natürliche Person angesehen, die direkt oder indirekt, insbesondere mittels Zuordnung zu einer Kennung wie einem Namen, zu einer Kennnummer, zu Standortdaten, zu einer Online-Kennung oder zu einem oder mehreren besonderen Merkmalen identifiziert werden kann, die Ausdruck der physischen, physiologischen, genetischen, psychischen, wirtschaftlichen, kulturellen oder sozialen Identität dieser natürlichen Person sind.\n" +
            "Cookies: Kleine Dateien, die es uns ermöglichen, auf Ihrem Endgerät spezifische, auf Sie, den Nutzer, bezogene Informationen zu ermitteln. Die Speicherung von Cookies können Sie über Ihre Browser-Einstellungen verhindern.\n";

}