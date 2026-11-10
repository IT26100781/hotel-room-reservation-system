package com.hotel.user.guest.servlet;

import com.hotel.user.guest.model.Guest;
import com.hotel.user.guest.model.RegularGuest;
import com.hotel.user.guest.service.GuestService;
import com.hotel.util.SessionUtil;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuestServletTests {
    @Test
    void profileUpdateUsesSessionIdInsteadOfSubmittedId() throws Exception {
        Guest guest = new RegularGuest("G01", "Guest", "guest@example.com", "hash");
        GuestService service = mock(GuestService.class);
        GuestProfileServlet servlet = new GuestProfileServlet();
        servlet.guestService = service;
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        SessionUtil.login(request.getSession(), guest);
        request.setParameter("id", "G99");
        request.setParameter("name", "Updated");
        request.setParameter("email", "new@example.com");
        when(service.updateGuestDetails(guest, "G01", "Updated", "new@example.com")).thenReturn(guest);
        servlet.doPost(request, response);
        verify(service).updateGuestDetails(guest, "G01", "Updated", "new@example.com");
        assertEquals("/profile?done=details", response.getRedirectedUrl());
    }

    @Test
    void filterRejectsGuestDeactivatedSinceLogin() throws Exception {
        Guest guest = new RegularGuest("G01", "Guest", "guest@example.com", "hash");
        Guest fresh = new RegularGuest("G01", "Guest", "guest@example.com", "hash");
        fresh.setActive(false);
        GuestService service = mock(GuestService.class);
        when(service.findGuestById(guest, "G01")).thenReturn(fresh);
        GuestAuthFilter filter = new GuestAuthFilter();
        ReflectionTestUtils.setField(filter, "guestService", service);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        SessionUtil.login(request.getSession(), guest);
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        assertEquals("/login?deactivated=1", response.getRedirectedUrl());
        assertNull(request.getSession(false));
        verifyNoInteractions(chain);
    }

    @Test
    void registrationRejectsMissingPasswordWithoutCallingService() throws Exception {
        GuestRegisterServlet servlet = new GuestRegisterServlet();
        servlet.guestService = mock(GuestService.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        assertEquals("Passwords do not match", request.getAttribute("error"));
        assertEquals("/WEB-INF/views/guest/register.jsp", response.getForwardedUrl());
        verifyNoInteractions(servlet.guestService);
    }
}
