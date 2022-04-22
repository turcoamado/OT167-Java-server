package com.alkemy.ong.controller;

import com.alkemy.ong.dto.TestimonialDto;
import com.alkemy.ong.exception.MessageInfo;
import com.alkemy.ong.exception.MessagesInfo;
import com.alkemy.ong.service.TestimonialService;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.validation.Valid;

import com.alkemy.ong.utils.SwaggerConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@Tag(name = "Testimonial", description = "What people say about us")
@RestController
@RequestMapping("/testimonials")
public class TestimonialController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TestimonialService testimonialService;

    @Operation(summary = "Create a new testimonial", description = "Create a new testimonial filling the params of the body and return a success or error message. \n"
            + "The required fields are: name and image. The name field can only be filled with non-numeric characters.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Testimonial was created",
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageInfo.class),
                            array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {
                                @ExampleObject(name = "Example 1", summary = "Testimonial created", description = "when the required fields are filled in, sends a testimonial message successfully created", value = SwaggerConstants.MODEL_TESTIMONIAL_CREATED),})}),
        @ApiResponse(responseCode = "400", description = "A param error",
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessagesInfo.class),
                            examples = {
                                @ExampleObject(name = "Example 1", summary = "Member param Error", description = "when required fields are not filled in, sends a 400 (Bad Request) error message", value = SwaggerConstants.MODEL_TESTIMONIAL_PARAM_ERROR),})})})
    @PostMapping
    public ResponseEntity<Map<String, String>> saveTestimonial(@Valid @RequestBody TestimonialDto testimonialDto) {
        testimonialService.save(testimonialDto);
        Map<String, String> message = new HashMap<>() {
            {
                put("message: ", messageSource.getMessage("testimonial.created.successfully", new Object[]{testimonialDto.getName()}, Locale.ENGLISH));
            }
        };
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @Operation(summary = "Update a testimonial by its id", description = "Find a testimonial with the id, fill the params of the body and return a member update or error message. \n"
            + "The required fields are: name and image. The name field can only be filled with non-numeric characters.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Testimonial was modifed",
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TestimonialDto.class),
                            examples = {
                                @ExampleObject(name = "Testimonial was update", summary = "Testimonial update", description = "when the required fields are completed, the user is updated and the entity update is sent.", value = SwaggerConstants.MODEL_TESTIMONIAL
                                )})}),
        @ApiResponse(responseCode = "400", description = "A param cannot be null",
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessagesInfo.class),
                            examples = {
                                @ExampleObject(name = "Example 1", summary = "Testimonial param Error", description = "when required fields are not filled in, sends a 400 (Bad Request) error message", value = SwaggerConstants.MODEL_TESTIMONIAL_PARAM_ERROR),})}),
        @ApiResponse(responseCode = "404", description = "Testimonial not found",
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageInfo.class),
                            examples = {
                                @ExampleObject(name = "Example error 404", summary = "Testimonial not found", description = "When you enter the id of a nonexistent testimonial in the route, it sends a message error 400 (Not Found).", value = SwaggerConstants.MODEL_TESTIMONIAL_ERROR_404
                                )})})})
    @PutMapping("/{id}")
    public ResponseEntity<TestimonialDto> update(@Parameter(description = "id of testimonial to be searched") @PathVariable Long id, @Validated @RequestBody TestimonialDto testimonialDto) {
        TestimonialDto result = this.testimonialService.update(id, testimonialDto);
        return ResponseEntity.ok().body(result);
    }

    @Operation(summary = "Delete a testimonial by its id", description = "Find a testimonial with the id and try delete it and return a success or error message")
    @ApiResponse(responseCode = "200", description = "Testimonial was deleted",
            content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = MessageInfo.class),
                        examples = {
                            @ExampleObject(name = "Testimonial deleted", summary = "Testimonial deleted", description = "The testimonial is successfully removed when found by id, then a success message is sent.", value = SwaggerConstants.MODEL_TESTIMONIAL_DELETE
                            )})})
    @ApiResponse(responseCode = "400", description = "Error for the character entered",
            content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = MessageInfo.class),
                        examples = {
                            @ExampleObject(name = "Example error 400", summary = "Error character", description = "When I enter an invalid character in the path it sends an error message 400 (Bad Request).", value = SwaggerConstants.MODEL_TESTIMONIAL_ERROR_400
                            )})})
    @ApiResponse(responseCode = "404", description = "Testimonial not found",
            content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = MessageInfo.class),
                        examples = {
                            @ExampleObject(name = "Example error 404", summary = "Testimonial not found", description = "When you enter the id of a nonexistent testimonial in the route, it sends a message error 400 (Not Found).", value = SwaggerConstants.MODEL_TESTIMONIAL_ERROR_404
                            )})})
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@Parameter(description = "id of testimonial to be searched") @PathVariable Long id) {
        this.testimonialService.delete(id);
        Map<String, String> message = new HashMap<>() {
            {
                put("message: ", messageSource
                        .getMessage("testimonial.delete.ok", new Object[]{id}, Locale.ENGLISH));
            }
        };
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @GetMapping
    public ResponseEntity<?> findAllNewsPag(@RequestParam(value = "page", required = true) String page, WebRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(testimonialService.findAllPag(Integer.parseInt(page), request));
    }
}
