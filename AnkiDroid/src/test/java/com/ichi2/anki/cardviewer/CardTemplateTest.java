/*
 *  Copyright (c) 2020 David Allison <davidallisongithub@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software
 *  Foundation; either version 3 of the License, or (at your option) any later
 *  version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ichi2.anki.cardviewer;

import android.content.Context;
import android.content.SharedPreferences;

import com.ichi2.anki.AnkiDroidApp;
import com.ichi2.anki.jsaddons.NpmUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

public class CardTemplateTest {
    @Mock
    private Context mMockContext;

    @Mock
    private SharedPreferences mMockSharedPreferences;

    MockedStatic<AnkiDroidApp> mMockAnkiDroidApp;
    MockedStatic<NpmUtils> mMockAddonsNpmUtils;

    @Before
    public void SetUp() {
        MockitoAnnotations.openMocks(this);

        mMockAnkiDroidApp = Mockito.mockStatic(AnkiDroidApp.class);
        mMockAddonsNpmUtils = Mockito.mockStatic(NpmUtils.class);

        when(mMockContext.getSharedPreferences(any(), anyInt()))
                .thenReturn(mMockSharedPreferences);

        when(mMockSharedPreferences.getBoolean("javascript_addons_support_prefs", false))
                .thenReturn(false);

        mMockAnkiDroidApp.when(() -> AnkiDroidApp.getSharedPrefs(mMockContext)).thenReturn(mMockSharedPreferences);
    }

    @After
    public void validate() {
        validateMockitoUsage();
        mMockAnkiDroidApp.close();
        mMockAddonsNpmUtils.close();
    }

    private static final String data = "<!doctype html>\n" +
            "<html class=\"mobile android linux js\">\n" +
            "    <head>\n" +
            "        <title>AnkiDroid Flashcard</title>\n" +
            "        <meta charset=\"utf-8\">\n" +
            "        <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/flashcard.css\">\n" +
            "        <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/chess.css\">\n" +
            "        <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/mathjax/mathjax.css\">\n" +
            "        <style>\n" +
            "        ::style::\n" +
            "        </style>\n" +
            "        ::script::\n" +
            "        <script src=\"file:///android_asset/mathjax/conf.js\"> </script>\n" +
            "        <script src=\"file:///android_asset/mathjax/MathJax.js\"> </script>\n" +
            "        <script src=\"file:///android_asset/scripts/card.js\" type=\"text/javascript\"> </script>\n" +
            "    </head>\n" +
            "    <body class=\"::class::\">\n" +
            "        <div id=\"content\">\n" +
            "        ::content::\n" +
            "        </div>\n" +
            "    </body>\n" +
            "</html>\n";

    @Test
    public void replaceTest() {
        // Method is sped up - ensure that it still works.
        String content = "foo";
        String style = "bar";
        String cardClass = "baz";
        String script = "script";
        String result = new CardTemplate(data, mMockContext).render(content, style, script, cardClass);

        assertThat(result, is(data.replace("::content::", content).replace("::style::", style).replace("::class::", cardClass).replace("::script::", script)));
    }

    @Test
    public void stressTest() {
        // At length = 10000000
        // ~500ms before
        // < 200 after
        int stringLength = 1000;
        String content = new String(new char[stringLength]).replace('\0', 'a');

        String ret = new CardTemplate(data, mMockContext).render(content, content, "", content);
    }

}
