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
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.DaoFactory;

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
	private CacheControl cc;
	@Context
	private UriInfo uriInfo;

	/**
	 * Construct ContactDao from DaoFactory.
	 */
	public ContactResource() {
		cc = new CacheControl();
		cc.setMaxAge(46800);
		dao = DaoFactory.getInstance().getContactDao();
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
	public Response getContact(@QueryParam("title") String query,
			@Context Request request) {
		GenericEntity<List<Contact>> ge = null;
		if (query != null) {
			ge = convertListToGE(dao.findByTitle(query));
		} else {
			ge = convertListToGE(dao.findAll());
		}
		if (!ge.getEntity().isEmpty()) {
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
	public Response getContactById(@PathParam("id") long id,
			@Context Request request) {
		Contact contact = dao.find(id);
		if (contact == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		EntityTag etag = attachEtag(contact);
		ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			builder = Response.ok(contact);
			builder.tag(etag);
		}
		builder.cacheControl(cc);
		return builder.build();
	}

	/**
	 * Create a new contact. If contact id is omitted or 0, the server will
	 * assign a unique ID and return it as the Location header.
	 * 
	 * @param element
	 *            element of JAXBElement
	 * @param uriInfo
	 *            information of URI
	 * @return response 201 CREATED if create success that show location header.
	 *         If same id response 409 CONFLICT, otherwise 400 BAD REQUEST
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response post(JAXBElement<Contact> element,
			@Context UriInfo uriInfo, @Context Request request) {
		Contact contact = element.getValue();
		if (dao.find(contact.getId()) != null) {
			return Response.status(Response.Status.CONFLICT).build();
		}

		EntityTag etag = attachEtag(contact);
		ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			if (!dao.save(contact)) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			URI uri = uriInfo.getAbsolutePathBuilder()
					.path(contact.getId() + "").build();
			builder = Response.created(uri);
			builder.tag(etag);
		}
		builder.cacheControl(cc);
		return builder.build();
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
	public Response putContact(@PathParam("id") long id,
			JAXBElement<Contact> element, @Context Request request) {
		Contact newContact = element.getValue();
		if (!(newContact.getId() == id)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		newContact.setId(id);
		Contact contact = dao.find(id);
		EntityTag etag = attachEtag(contact);
		ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			if (!dao.update(newContact)) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			URI uri = uriInfo.getAbsolutePath();
			String message = "Location: " + uri + newContact.getId();
			builder = Response.ok(message);
			builder.tag(etag);
		}
		builder.cacheControl(cc);
		return builder.build();
	}

	/**
	 * Delete a contact with matching id
	 * 
	 * @param id
	 *            identifier of contact
	 * @return response 200 OK if contact can delete, otherwise response 404 NOT
	 *         FOUND
	 */
	@DELETE
	@Path("{id}")
	public Response deleteContact(@PathParam("id") long id,
			@Context Request request) {
		Contact contact = dao.find(id);
		if (contact == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		EntityTag etag = attachEtag(contact);
		ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			dao.delete(id);
			builder = Response.ok();
		}
		builder.cacheControl(cc);
		return builder.build();
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

	/**
	 * Construct Etag from contact
	 * 
	 * @param contact
	 * @return etag Entity tag of contact
	 */
	public EntityTag attachEtag(Contact contact) {
		EntityTag etag = new EntityTag(contact.sha1());
		return etag;
	}
}
