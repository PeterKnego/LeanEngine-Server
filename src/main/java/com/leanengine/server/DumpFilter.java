package com.leanengine.server;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * This a straightforward request and reply, header & body dump filter. Usable when trableshooting.
 * Just enable it in web.xml with {@code<filter>}, {@code<filter-mapping>} and {@code<init-param>}:<br/><br/>
 * {@code<filter>}<br/>
 * {@code     <filter-name>DumpFilter</filter-name>}<br/>
 * {@code     <filter-class>com.leanengine.server.DumpFilter</filter-class>}<br/>
 * {@code         <init-param>}<br/>
 * {@code               <param-name>dumpRequest</param-name>}<br/>
 * {@code             <param-value>true</param-value>}<br/>
 * {@code         </init-param>}<br/>
 * {@code         <init-param>}<br/>
 * {@code             <param-name>dumpResponse</param-name>}<br/>
 * {@code             <param-value>true</param-value>}<br/>
 * {@code         </init-param>}<br/>
 * {@code         <init-param>}<br/>
 * {@code             <param-name>dumpHeader</param-name>}<br/>
 * {@code             <param-value>true</param-value>}<br/>
 * {@code         </init-param>}<br/>
 * {@code     </filter>}<br/>
 * {@code     <filter-mapping>}<br/>
 * {@code         <filter-name>DumpFilter</filter-name>}<br/>
 * {@code         <url-pattern>/some/url/*</url-pattern>}<br/>
 * {@code     </filter-mapping>}<br/>
 */
public class DumpFilter implements Filter {

    private static final Logger log = Logger.getLogger(DumpFilter.class.getName());

    private static class ByteArrayServletStream extends ServletOutputStream {

        ByteArrayOutputStream baos;

        ByteArrayServletStream(ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        public void write(int param) throws IOException {
            baos.write(param);
        }
    }

    private static class ByteArrayPrintWriter {

        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

        private PrintWriter pw = new PrintWriter(baos);

        private ServletOutputStream sos = new ByteArrayServletStream(baos);

        public PrintWriter getWriter() {
            return pw;
        }

        public ServletOutputStream getStream() {
            return sos;
        }

        byte[] toByteArray() {
            return baos.toByteArray();
        }
    }

    private class BufferedServletInputStream extends ServletInputStream {

        ByteArrayInputStream bais;

        public BufferedServletInputStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        public int available() {
            return bais.available();
        }

        public int read() {
            return bais.read();
        }

        public int read(byte[] buf, int off, int len) {
            return bais.read(buf, off, len);
        }

    }

    private class BufferedRequestWrapper extends HttpServletRequestWrapper {

        ByteArrayInputStream bais;

        ByteArrayOutputStream baos;

        BufferedServletInputStream bsis;

        byte[] buffer;

        public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
            super(req);
            InputStream is = req.getInputStream();
            baos = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int letti;
            while ((letti = is.read(buf)) > 0) {
                baos.write(buf, 0, letti);
            }
            buffer = baos.toByteArray();
        }

        public ServletInputStream getInputStream() {
            try {
                bais = new ByteArrayInputStream(buffer);
                bsis = new BufferedServletInputStream(bais);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return bsis;
        }

        public byte[] getBuffer() {
            return buffer;
        }

    }

    private boolean dumpRequest;
    private boolean dumpResponse;
    private boolean dumpHeader;

    public void init(FilterConfig filterConfig) throws ServletException {
        dumpRequest = Boolean.valueOf(filterConfig.getInitParameter("dumpRequest"));
        dumpResponse = Boolean.valueOf(filterConfig.getInitParameter("dumpResponse"));
        dumpHeader = Boolean.valueOf(filterConfig.getInitParameter("dumpHeader"));
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpRequest);

        if (dumpRequest) {
//            log.info("REQUEST -> " + new String(bufferedRequest.getBuffer()));
            System.out.println("REQUEST URL: " + httpRequest.getServletPath());
            System.out.println("REQUEST -> " + new String(bufferedRequest.getBuffer()));
        }

        if (dumpHeader) {
//            log.info("  REQUEST HEADER:");
            System.out.println("  REQUEST HEADER:");

            Enumeration headers = httpRequest.getHeaderNames();
            while (headers.hasMoreElements()) {
                String header = (String) headers.nextElement();
                System.out.println(header + "=" + httpRequest.getHeader(header));
            }
        }


        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final ByteArrayPrintWriter pw = new ByteArrayPrintWriter();
        HttpServletResponse wrappedResp = new HttpServletResponseWrapper(response) {
            public PrintWriter getWriter() {
                return pw.getWriter();
            }

            public ServletOutputStream getOutputStream() {
                return pw.getStream();
            }

        };

        filterChain.doFilter(bufferedRequest, wrappedResp);

        byte[] bytes = pw.toByteArray();
        response.getOutputStream().write(bytes);
        if (dumpResponse) {
//            log.info("RESPONSE -> " + new String(bytes));
            System.out.println("RESPONSE -> " + new String(bytes));
        }
    }

    public void destroy() {
    }

}
