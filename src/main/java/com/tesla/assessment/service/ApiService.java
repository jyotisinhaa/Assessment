package com.tesla.assessment.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class ApiService {
    public static final String ERROR_KEY = "error";
    public static final String ERROR_VALUE = "bad request";
    @Autowired
    private ErrorService errorService;

    public Map<String,Object> handleResponse(String[] chunk){
        Map<String, Object> response = new HashMap<>();
        try{
            int deviceId = Integer.parseInt(chunk[0]);
            long epoch = Long.parseLong(chunk[1]);
            double temperature =  (chunk[3].contains("."))?Double.parseDouble(chunk[3]):0.0;
            if (deviceId==0||epoch==0||temperature==0.0){
                errorService.addError(String.join(":", chunk));
                return addErrorToMap(ERROR_KEY,ERROR_VALUE);
            }
            //Check for the over temperature
            if(temperature>= 90){
                String formattedTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(epoch);
                response.put("overtemp", true);
                response.put("device_id", deviceId);
                response.put("formatted_time", formattedTime);
            }else{
                response.put("overtemp", "false");
            }

        }
        catch (NumberFormatException e) {
            errorService.addError(String.join(":", chunk));
            return addErrorToMap(ERROR_KEY,ERROR_VALUE);

        }
        return response;
    }
    public Map<String, Object> addErrorToMap(String key, Object value) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put(key, value);
        return errorMap;
    }

    public ResponseEntity<Map<String, Object>> handleBadRequest(Map<String, Object> error) {
        errorService.addError(String.valueOf(error));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(addErrorToMap(ERROR_KEY,ERROR_VALUE));
    }
}
