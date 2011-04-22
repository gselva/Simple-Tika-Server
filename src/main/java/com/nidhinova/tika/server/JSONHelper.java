/*
 * This file is licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nidhinova.tika.server;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import com.google.gson.Gson;
import org.apache.tika.metadata.Metadata;
/**
 * Helper to convert a Tika Metadata object to JSON. Uses Gson library.
 * @author github.com/gselva
 *
 */
public class JSONHelper {
	private static Gson gson = new Gson();
	
	public static String metadataToJson(Metadata metadata) {
		String[] names = metadata.names();
		Arrays.sort(names);

		int i = 0;
		String json_comma = ",";
		StringBuilder buffer = new StringBuilder();
		buffer.append("{ ");
		for (String name : names) {
			if (i < names.length - 1)
				json_comma = ",";
			else
				json_comma = "";

			String value = null;
			if (metadata.isMultiValued(name)) {
				value = parseMetadataValues(metadata
						.getValues(name));
			} else {
				value = parseMetadataValue(metadata.get(name));
			}
			
			//add to metadata only if there is some useful value
			if(value!=null && !value.trim().equals("")) {
			buffer.append(" ").append(gson.toJson(name)).append(":")
					.append(value).append(json_comma);
			}
			i++;
		}
		buffer.append(" }");
		return buffer.toString();
	}
	
	public static String toJSON(String value) {
		return gson.toJson(value);
	}
	
	private static String parseMetadataValue(String value) {
		if (isNumeric(value)) {
			try {
				return NumberFormat.getInstance().parse(value).toString();
			} catch (ParseException e) {
				//Expected. We'll ignore this exception
			}
		}

		return gson.toJson(value);
	}

	private static String parseMetadataValues(String[] values) {
		// TODO if array elements are numbers, we should do something more appropriate
		return gson.toJson(values);
	}

	/**
	 * Works out of a string could be parsed a Number
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		boolean isnumeric = false;
		try{
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		isnumeric=(str.length() == pos.getIndex()?true: false);
		}catch(Exception e) {
			isnumeric=false;
		}
		return isnumeric;
	}

}
