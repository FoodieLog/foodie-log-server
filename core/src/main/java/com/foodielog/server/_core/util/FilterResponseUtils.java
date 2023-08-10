package com.foodielog.server._core.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodielog.server._core.error.exception.Exception401;
import com.foodielog.server._core.error.exception.Exception403;

public class FilterResponseUtils {
	public static void unAuthorized(HttpServletResponse resp, Exception401 e) throws IOException {
		resp.setStatus(e.status().value());
		resp.setContentType("application/json; charset=utf-8");
		ObjectMapper om = new ObjectMapper();
		String responseBody = om.writeValueAsString(e.body());
		resp.getWriter().println(responseBody);
	}

	public static void forbidden(HttpServletResponse resp, Exception403 e) throws IOException {
		resp.setStatus(e.status().value());
		resp.setContentType("application/json; charset=utf-8");
		ObjectMapper om = new ObjectMapper();
		String responseBody = om.writeValueAsString(e.body());
		resp.getWriter().println(responseBody);
	}
}
