package org.project.nuwabackend.api.workspace;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.global.type.SuccessMessage;
import org.project.nuwabackend.service.workspace.WorkSpaceDeleteService;
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
// TODO: test code
public class WorkSpaceDeleteController {

    private final WorkSpaceDeleteService workSpaceDeleteService;
    private final GlobalService globalService;

    @DeleteMapping("/workspace/{workSpaceId}")
    public ResponseEntity<Object> deleteWorkSpace(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                  @MemberEmail String email) {
        workSpaceDeleteService.deleteWorkSpace(email, workSpaceId);

        GlobalSuccessResponseDto<Object> deleteWorkSpaceSuccessResponse =
                globalService.successResponse(SuccessMessage.DELETE_WORK_SPACE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(deleteWorkSpaceSuccessResponse);
    }
}
