/*
Copyright 2013 Andrew Joiner

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package uk.co.tekkies.readings.fragment;

import uk.co.tekkies.readings.R;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class PassageFragment extends Fragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        String passage = args.getString("passage");
        TextView textView1 = (TextView) (view.findViewById(R.id.textView1));
        String html = render(getPassageXml(passage));
        textView1.setText(Html.fromHtml(html));
        super.onViewCreated(view, savedInstanceState);
    }

    protected String render(String html) {
        html = html.replace("<v>", "<sup><font color=\"red\">");
        html = html.replace("</v>", "</font></sup>");
        return reRender(html);
    }

    protected String reRender(String html) {
        html = html.replace("<summary>", "<i><font color=\"blue\">");
        html = html.replace("</summary>", "</font></i>");
        html = html.replace("<v>", "<sup><font color=\"red\">");
        html = html.replace("</v>", "</font></sup>");
        return html;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.passage_fragment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_readings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_summary:
            // doToggleSummary();
            return true;

        default:
            return false;
        }
    }

    
    protected String getPassageXml(String passage) {
        String passageXml = "Error";
        String[] row = new String[] { "passage" };
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.parse("content://uk.co.tekkies.plugin.bible.kjv/passage/" + passage), 
                row, 
                "", 
                row,
                "");
        if (cursor.moveToFirst()) {
            passageXml = cursor.getString(cursor.getColumnIndex("passage"));
        }
        return passageXml;
    }
}