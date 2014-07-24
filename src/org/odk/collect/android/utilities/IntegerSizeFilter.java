package org.odk.collect.android.utilities;

import android.text.InputFilter;
import android.text.Spanned;

public class IntegerSizeFilter implements InputFilter {

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		String destString = dest.toString();
		if (source.equals("") || destString.equals("")) {
			return null; //If the source or destination strings are empty, can leave as is
		}
		String part1 = destString.substring(0, dstart);
		String part2 = destString.substring(dend);
		String newString = part1 + (String)source + part2;
		
		try {
			Integer x = Integer.parseInt((String)newString);
			return null; //keep original
		}
		catch (NumberFormatException e) {
			return ""; //don't allow edit that was just made
		}
	}

}
