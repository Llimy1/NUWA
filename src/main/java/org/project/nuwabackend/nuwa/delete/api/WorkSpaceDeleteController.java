package org.project.nuwabackend.nuwa.delete.api;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.annotation.custom.MemberEmail;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.delete.service.WorkSpaceDeleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkSpaceDeleteController {

    private final WorkSpaceDeleteService workSpaceDeleteService;
    private final GlobalService globalService;

    @DeleteMapping("/workspace/{workSpaceId}")
    public ResponseEntity<Object> deleteWorkSpace(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                  @MemberEmail String email) {
        String successMessage = workSpaceDeleteService.deleteWorkSpace(email, workSpaceId);

        GlobalSuccessResponseDto<Object> deleteWorkSpaceSuccessResponse =
                globalService.successResponse(successMessage, null);

        return ResponseEntity.status(OK).body(deleteWorkSpaceSuccessResponse);
    }
}
