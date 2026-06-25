package com.info85.maiscolares;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Mares {

    private TableLayout tableLayout;

    public Mares(TableLayout tableLayout) {
        this.tableLayout = tableLayout;
    }

    public void fetchData() {
        new FetchMaresData().execute();
    }

    private class FetchMaresData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://www.capitao-das-mares.com/mar%C3%A9/Colares/").get();
                return doc.select("#i_donnesLongue").html();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                Document document = Jsoup.parse(result);

                Element table = document.select("table").first();

                if (table != null) {
                    Elements rows = table.select("tr");

                    for (Element row : rows) {
                        Elements cells = row.select("th,td");
                        TableRow tableRow = new TableRow(tableLayout.getContext());

                        for (Element cell : cells) {

                            TextView textView = new TextView(tableLayout.getContext());
                            String text = cell.text();
                            if (text.equals("Altura da água (m)")) {
                                text = "Altura";
                            }
                            if (text.equals("Maré Baixa")){
                                text = "Baixa";
                            }
                            if (text.equals("Maré Alta")){
                                text = "Alta";
                            }

                            textView.setText(" | " + text);
                            textView.setPadding(10, 8, 10, 8);

                            Typeface customFont = ResourcesCompat.getFont(tableLayout.getContext(), R.font.jmhtypewriter);
                            textView.setTypeface(customFont);

                            if (text.equals("Maré") || text.equals("Hora") || text.equals("Altura")) {
                                textView.setBackgroundColor(tableLayout.getContext().getResources().getColor(android.R.color.white));
                                textView.setTextColor(tableLayout.getContext().getResources().getColor(android.R.color.white));
                            }

                            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                            textView.setTextColor(tableLayout.getContext().getResources().getColor(android.R.color.black));
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                            textView.setLayoutParams(layoutParams);

                            tableRow.addView(textView);
                        }

                        tableLayout.addView(tableRow);
                    }
                }
            }
        }
    }
}
