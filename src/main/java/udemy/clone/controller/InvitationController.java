package udemy.clone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import udemy.clone.model.invitations.InvitationDto;
import udemy.clone.service.InvitationService;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping("/send")
    public InvitationDto sendInvitation(@Validated @RequestBody InvitationDto invitationDto) {
        return invitationService.sendInvitation(invitationDto);
    }

    @PutMapping("/accept/{invitationId}")
    public InvitationDto acceptInvitation(@PathVariable String invitationId) {
        return invitationService.acceptInvitation(invitationId);
    }

    @PutMapping("/reject/{invitationId}")
    public InvitationDto rejectInvitation(@PathVariable String invitationId) {
        return invitationService.rejectInvitation(invitationId);
    }

    @DeleteMapping("/delete/{invitationId}")
    public void cancelInvitation(@PathVariable String invitationId) {
        invitationService.cancelInvitation(invitationId);
    }

}
