package com.dekaisheng.courier.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
public class StringUtils {

	public static boolean isEmpty(String s) {
		if (s == null) {
			return true;
		}

		if (s.length() < 1) {
			return true;
		}

		return false;
	}
	
	public static boolean isBlank(String s) {
		if (isEmpty(s)) {
			return true;
		}

		if (isEmpty(s.trim())) {
			return true;
		}

		return false;
	}
	
	
	/**
	 * 验证有效是否有效
	 * 
	 * @param mail
	 * @return
	 */
	public  static boolean isValidEmail(String mail) {
		Pattern pattern = Pattern
				.compile("^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})");
		Matcher mc = pattern.matcher(mail);
		return mc.matches();
	}
}
