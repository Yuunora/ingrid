package com.ingrid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingrid.osmr.OsmrException;
import com.ingrid.osmr.OsmrService;
import com.ingrid.osmr.RouteInfo;
import com.ingrid.routes.Routes;
import com.ingrid.routes.Service;

@WebMvcTest(Service.class)
public class ServiceTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private OsmrService service;

    private final String pos = "13.3,52.5";
    private final String otherPos = "14.4,42.3";
    private final double minDuration = 0.0;
    private final double minDistance = 0.0;
    private final double maxDuration = 4.9;
    private final double maxDist = 5.6;

    @Test
    public void routesWithDuplicate() throws Exception {

        final var infoPos = new RouteInfo(pos, minDuration, minDistance);
        final var infoOtherPos = new RouteInfo(otherPos, maxDuration, maxDist);
        final var expectedRoutes = new Routes(pos, Arrays.asList(infoPos, infoOtherPos));
        try {
            Mockito.when(service.findRoute(pos, pos)).thenReturn(infoPos);
            Mockito.when(service.findRoute(pos, otherPos)).thenReturn(infoOtherPos);
            var request = MockMvcRequestBuilders.get("/routes");
            request.param("src", pos);
            request.param("dst", pos); // Duplicate
            request.param("dst", otherPos);
            final var result = mvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
            final var content = result.getResponse().getContentAsString();
            final var mapper = new ObjectMapper();
            final var expectedContent = mapper.writeValueAsString(expectedRoutes);
            assertEquals(expectedContent, content);
        } catch (Exception | OsmrException e) {
            fail(e);
        }
    }

    @Test
    public void routesWhenOsmrThrows() {
        try {
            Mockito.when(service.findRoute(pos, otherPos)).thenThrow(new OsmrException("Osmr Exception"));
            var request = MockMvcRequestBuilders.get("/routes");
            request.param("src", pos);
            request.param("dst", otherPos);
            request.param("dst", pos);

            final var result = mvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();
            assertTrue(result.getResponse().getContentAsString().isEmpty());
        } catch (OsmrException | Exception e) {
            fail(e);
        }
    }

}
