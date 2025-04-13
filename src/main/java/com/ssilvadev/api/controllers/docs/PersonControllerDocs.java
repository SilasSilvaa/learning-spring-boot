package com.ssilvadev.api.controllers.docs;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.ssilvadev.api.data.dto.PersonDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface PersonControllerDocs {
        @Operation(summary = "Find all People", description = "Find all People", tags = "People", responses = {
                        @ApiResponse(description = "Success", responseCode = "200", content = {
                                        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PersonDTO.class)))
                        }),
                        @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                        @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
        ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                        @RequestParam(value = "size", defaultValue = "12") Integer size,
                        @RequestParam(value = "direction", defaultValue = "asc") String direction);

        @Operation(summary = "Find People by name", description = "Find People by name", tags = "People", responses = {
                        @ApiResponse(description = "Success", responseCode = "200", content = {
                                        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PersonDTO.class)))
                        }),
                        @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                        @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
        ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findByName(
                        @PathVariable("firstName") String firstName,
                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                        @RequestParam(value = "size", defaultValue = "12") Integer size,
                        @RequestParam(value = "direction", defaultValue = "asc") String direction);

        @Operation(summary = "Find a Person", description = "Find a Person", tags = "People", responses = {
                        @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                        @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                        @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
        PersonDTO findById(@PathVariable Long id);

        @Operation(summary = "Create a Person", description = "Create a Person", tags = "People", responses = {
                        @ApiResponse(description = "Success", responseCode = "201", content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                        @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                        @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
        PersonDTO create(@RequestBody PersonDTO person);

        @Operation(summary = "Update a Person", description = "Update a Person", tags = "People", responses = {
                        @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                        @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                        @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
        PersonDTO update(@RequestBody PersonDTO person);

        @Operation(summary = "Delete a Person", description = "Delete a Person", tags = "People", responses = {
                        @ApiResponse(description = "Success", responseCode = "204", content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                        @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                        @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
        ResponseEntity<?> delete(@PathVariable Long id);

        @Operation(summary = "Disable a Person", description = "Disable a Person", tags = "People", responses = {
                        @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                        @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                        @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
        PersonDTO disable(@PathVariable Long id);
}
