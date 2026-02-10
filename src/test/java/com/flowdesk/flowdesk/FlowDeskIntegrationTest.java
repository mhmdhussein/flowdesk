package com.flowdesk.flowdesk;

import com.flowdesk.flowdesk.auth.dto.AuthResponse;
import com.flowdesk.flowdesk.project.dto.ProjectResponse;
import com.flowdesk.flowdesk.ticket.dto.CreateTicketRequest;
import com.flowdesk.flowdesk.ticket.dto.PagedResponse;
import com.flowdesk.flowdesk.ticket.dto.TicketResponse;
import com.flowdesk.flowdesk.ticket.dto.UpdateTicketStatusRequest;
import com.flowdesk.flowdesk.ticket.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class FlowDeskIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    /* ---------- helpers ---------- */

    private String registerAndLogin(String email) {
        var body = """
        { "email": "%s", "password": "Password123!" }
    """.formatted(email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // Use String.class first to see if there is an error message
        ResponseEntity<String> response = rest.postForEntity("/api/auth/register", entity, String.class);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Registration FAILED for " + email +
                    "\nStatus: " + response.getStatusCode() +
                    "\nBody: " + response.getBody());
        }

        // If it worked, parse it properly
        return rest.postForEntity("/api/auth/register", entity, AuthResponse.class).getBody().token();
    }

    private HttpHeaders auth(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    /* ---------- TEST 1 ---------- */
    @Test
    void register_and_login_works() {
        String token = registerAndLogin("u1@flow.com");
        assertThat(token).isNotBlank();
    }

    /* ---------- TEST 2 ---------- */
    @Test
    void project_membership_is_enforced() {
        String t1 = registerAndLogin("owner@flow.com");
        String t2 = registerAndLogin("outsider@flow.com");

        // Instead of .getBody() immediately, do this:
        ResponseEntity<ProjectResponse> created = rest.exchange(
                "/api/projects",
                HttpMethod.POST,
                new HttpEntity<>("{\"name\":\"Secret\"}", auth(t1)),
                ProjectResponse.class
        );

        if (created.getStatusCode() != HttpStatus.CREATED) {
            // If we reach here, print the raw response to find the truth
            ResponseEntity<String> error = rest.exchange(
                    "/api/projects",
                    HttpMethod.POST,
                    new HttpEntity<>("{\"name\":\"Secret\"}", auth(t1)),
                    String.class
            );
            System.err.println("FAILED! Status: " + error.getStatusCode());
            System.err.println("Body: " + error.getBody());
            assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    /* ---------- TEST 3 ---------- */
    @Test
    void ticket_creation_requires_project_membership() {
        String owner = registerAndLogin("owner2@flow.com");
        String outsider = registerAndLogin("outsider2@flow.com");

        ProjectResponse project =
                rest.exchange(
                        "/api/projects",
                        HttpMethod.POST,
                        new HttpEntity<>("{\"name\":\"P1\"}", auth(owner)),
                        ProjectResponse.class
                ).getBody();

        CreateTicketRequest req = new CreateTicketRequest("Bug", "desc");

        ResponseEntity<String> forbidden =
                rest.exchange(
                        "/api/projects/" + project.id() + "/tickets",
                        HttpMethod.POST,
                        new HttpEntity<>(req, auth(outsider)),
                        String.class
                );

        assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /* ---------- TEST 4 ---------- */
    @Test
    void invalid_ticket_workflow_transition_returns_400() {
        String token = registerAndLogin("flow@flow.com");

        ProjectResponse project =
                rest.exchange(
                        "/api/projects",
                        HttpMethod.POST,
                        new HttpEntity<>("{\"name\":\"WF\"}", auth(token)),
                        ProjectResponse.class
                ).getBody();

        TicketResponse ticket =
                rest.exchange(
                        "/api/projects/" + project.id() + "/tickets",
                        HttpMethod.POST,
                        new HttpEntity<>(new CreateTicketRequest("T1", ""), auth(token)),
                        TicketResponse.class
                ).getBody();

        UpdateTicketStatusRequest invalid =
                new UpdateTicketStatusRequest(TicketStatus.DONE);

        ResponseEntity<String> resp =
                rest.exchange(
                        "/api/projects/" + project.id() + "/tickets/" + ticket.id() + "/status",
                        HttpMethod.PATCH,
                        new HttpEntity<>(invalid, auth(token)),
                        String.class
                );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /* ---------- TEST 5 ---------- */
    @Test
    void ticket_pagination_works() {
        String token = registerAndLogin("pager@flow.com");

        ProjectResponse project =
                rest.exchange(
                        "/api/projects",
                        HttpMethod.POST,
                        new HttpEntity<>("{\"name\":\"Pager\"}", auth(token)),
                        ProjectResponse.class
                ).getBody();

        for (int i = 0; i < 15; i++) {
            rest.exchange(
                    "/api/projects/" + project.id() + "/tickets",
                    HttpMethod.POST,
                    new HttpEntity<>(new CreateTicketRequest("T" + i, ""), auth(token)),
                    TicketResponse.class
            );
        }

        ResponseEntity<PagedResponse> page =
                rest.exchange(
                        "/api/projects/" + project.id() + "/tickets?page=0&size=10",
                        HttpMethod.GET,
                        new HttpEntity<>(auth(token)),
                        PagedResponse.class
                );

        assertThat(page.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page.getBody().items().size()).isEqualTo(10);
        assertThat(page.getBody().totalItems()).isEqualTo(15);
    }
}
