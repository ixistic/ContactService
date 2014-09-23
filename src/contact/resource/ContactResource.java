package contact.resource;

import java.net.URI;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.DaoFactory;
import contact.service.mem.MemDaoFactory;

/**
 * ContactResource provides RESTful web resources using JAX-RS annotations to
 * map requests to request handling code, and to inject resources into code.
 * 
 * @author Veerapat Threeravipark 5510547022
 * 
 */
@Path("/contacts")
@Singleton
public class ContactResource {
	private ContactDao dao;
	@Context
	UriInfo uriInfo;

	/**
	 * Construct ContactDao from DaoFactory.
	 */
	public ContactResource() {
		dao = MemDaoFactory.getInstance().getContactDao();
		System.out.println("Initial ContactDao.");
	}

	/**
	 * Get a list of all contacts or Get contact(s) whose title contains the
	 * query string (substring match).
	 * 
	 * @param query
	 *            is query string (title)
	 * @return response 200 OK if result not null that show list of result
	 *         contacts. If result is null response 404 NOT FOUND
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getContact(@QueryParam("title") String query) {
		GenericEntity<List<Contact>> ge = null;
		if (query != null) {
			ge = convertListToGE(dao.findByTitle(query));
		} else {
			ge = convertListToGE(dao.findAll());
		}
		if (ge != null) {
			return Response.ok(ge).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();

	}

	/**
	 * Get a contact by id.
	 * 
	 * @param id
	 *            identifier of contact
	 * @return response 200 OK if result not null that show contact. If result
	 *         is null response 404 NOT FOUND
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getContactById(@PathParam("id") String id) {
		// if(id != null){
		Contact contact = dao.find(Long.parseLong(id));
		if (contact != null) {
			return Response.ok(contact).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	/**
	 * Create a new contact. If contact id is omitted or 0, the server will
	 * assign a unique ID and return it as the Location header.
	 * 
	 * @param element
	 * @param uriInfo
	 * @return response 201 CREATED if create success that show location header.
	 *         If same id response 409 CONFLICT, otherwise 400 BAD REQUEST
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response post(JAXBElement<Contact> element, @Context UriInfo uriInfo) {
		Contact contact = element.getValue();
		if (dao.find(contact.getId()) != null) {
			return Response.status(Response.Status.CONFLICT).build();
		} else if (dao.save(contact)) {
			URI uri = uriInfo.getAbsolutePathBuilder()
					.path(contact.getId() + "").build();
			return Response.created(uri).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	/**
	 * Update a contact. Only update the attributes supplied in request body.
	 * 
	 * @param id
	 *            identifier of contact
	 * @param element
	 *            xml file in JAXBElement for unmarshal data
	 * @return response 200 OK if contact can update, if invalid data response
	 *         400 BAD REQUEST, otherwise response 404 NOT FOUND
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response putContact(@PathParam("id") String id,
			JAXBElement<Contact> element) {
		Contact contact = element.getValue();
		if (!(contact.getId() + "").equals(id)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		contact.setId(Long.parseLong(id));
		if (dao.update(contact)) {
			URI uri = uriInfo.getAbsolutePath();
			String message = "Location: " + uri + contact.getId();
			return Response.ok(message).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	/**
	 * Delete a contact with matching id
	 * 
	 * @param id
	 *            identifier of contact
	 * @return response 200 OK if contact can delete or otherwise
	 */
	@DELETE
	@Path("{id}")
	public Response deleteContact(@PathParam("id") String id) {
		dao.delete(Long.parseLong(id));
		return Response.ok().build();
	}

	/**
	 * Create an instance directly by supplying the generic type information
	 * with the entity.
	 * 
	 * @param contacts
	 *            list of contacts
	 * @return generic entity
	 */
	public GenericEntity<List<Contact>> convertListToGE(List<Contact> contacts) {
		GenericEntity<List<Contact>> ge = new GenericEntity<List<Contact>>(
				contacts) {
		};
		return ge;
	}
}
