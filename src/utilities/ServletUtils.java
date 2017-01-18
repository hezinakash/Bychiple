package utilities;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ServletUtils 
{
	public static void doJsonResponse(HttpServletResponse res, Object obj)
	{
		String jsonResponse = "";
		
		try (PrintWriter out = res.getWriter())
		{    
			Gson gson = new Gson();
			jsonResponse = gson.toJson(obj);
			out.print(jsonResponse);
			out.flush();
		} catch (IOException e) {
		}
	}
}
