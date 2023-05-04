package com.tesla.assessment.controller;
import com.tesla.assessment.service.ApiService;
import com.tesla.assessment.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {
    @Autowired
    private final ErrorService errorService;
    @Autowired
    private final ApiService apiService;
    public ApiController(ErrorService errorService, ApiService apiService) {
        this.errorService = errorService;
        this.apiService = apiService;
    }

    /**
     *  Retrieve all data strings that have been incorrectly formatted.
     *  Moreover, in cases where the input is null or invalid, the endpoint includes both the data and data string.
     *
     *  @param data Input provided by the user
     * @return A ResponseEntity with a success status and a map containing the following properties:
     *         - If the temperature is at or over 90, return {"overtemp": true, "device_id": __device_id__, "formatted_time": __formatted_time__}.
     *         - If the temperature is less that 90, return {"overtemp": false}
     * @throws HttpMessageNotReadableException If the request is not valid
     */

    @PostMapping(value = "/temp")
    public ResponseEntity<Map<String, Object>> postData(@RequestBody(required = false) Map<String, Object> data){

        // If the data is null,empty, doesn't contain "data" parameter then throw the error
        if (data == null || data.isEmpty() || data.get("data") == null) {
            return apiService.handleBadRequest(data);
        }
        try{
            String dataString = (String) data.get("data");
            if ( dataString == null || dataString.isEmpty()  || dataString.trim().isEmpty()){
                return apiService.handleBadRequest(data);
            }
            String[] chunk = dataString.split(":");
            //If the length of the chunk is not 4 or the third element doesn't contain 'Temperature' keyword then throw an error
            if (chunk.length!=4 || !chunk[2].contains("'Temperature'")){
                errorService.addError(dataString);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "bad request"));
            }
            return ResponseEntity.badRequest().body(apiService.handleResponse(chunk));
        }
        catch (HttpMessageNotReadableException e) {
            return ResponseEntity.badRequest().body(apiService.addErrorToMap("error","bad request"));
        }
    }

    /**
     *  Retrieve all data strings that have been incorrectly formatted.
     *  Moreover, in cases where the input is null or invalid, the endpoint includes both the data and data string.
     *
     * @return A ResponseEntity with a success status and a map containing a list of all errors.
     */
    @GetMapping("/errors")
    public ResponseEntity<Map<String, Object>> getErrors(){
        List<String> errors = new ArrayList<>(errorService.getErrors());
        return ResponseEntity.ok(Map.of("errors", errors));
    }

    /**
     * Clear the error list buffer and returns a success response with a message
     *
     * @return A ResponseEntity with a success status and a map containing a success message.
     */
    @DeleteMapping("/errors")
    public ResponseEntity<Map<String, Object>> formatErrors() {
        errorService.getErrors().clear();
        return ResponseEntity.ok(apiService.addErrorToMap("message", "cleared error buffer"));
    }

}
