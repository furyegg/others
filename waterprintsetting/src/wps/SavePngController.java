package wps;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

@SuppressWarnings("serial")
public class SavePngController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String imgBase64Str = req.getParameter("imageBase64Data");
		
		byte[] imgBytes = Base64.decodeBase64(imgBase64Str);
		ByteArrayInputStream bais = new ByteArrayInputStream(imgBytes);
		BufferedImage bimg = ImageIO.read(bais);
		
		String path = getServletContext().getRealPath("/");
		String fileFullName = path + "wps.png";
		
		ImageIO.write(bimg, "png", new File(fileFullName));
		
		PrintWriter out = resp.getWriter();
		out.print("<script type='text/javascript'>alert('Export png image successfully!')</script>");
		System.out.println("Export image: " + fileFullName);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
