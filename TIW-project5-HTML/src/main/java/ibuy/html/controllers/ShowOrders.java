package ibuy.html.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import ibuy.html.beans.Order;
import ibuy.html.beans.OrderItem;
import ibuy.html.beans.User;
import ibuy.html.dao.OrderDAO;
import ibuy.html.utilities.ConnectionHandler;

/**
 * Servlet implementation class ShowOrders
 */
@WebServlet("/ShowOrders")
public class ShowOrders extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowOrders() {
        super();
        // TODO Auto-generated constructor stub
    }

    //inizializzazione connessione e context
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		List<Order> myorders = new ArrayList<Order>();
		List<OrderItem> order_items = new ArrayList<OrderItem>();
		
		OrderDAO orders = new OrderDAO(connection);
		//cerco i miei ordini
		myorders = orders.findOrderByUserid(user.getId());
		//non ho ordini
		if (myorders == null) {
			String path = "/WEB-INF/myorders.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("MessageKo", "Seems that you didn't order anything yet....");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		} else { //ho ordini --> cerco i dettagli degli item per ordine
			for (Order i : myorders) {
				order_items.addAll(orders.findItemsByOrderId(i.getOrderId()));
			}
			String path = "/WEB-INF/myorders.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("orders", myorders);
			ctx.setVariable("orderitems", order_items);
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
