package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import udemy.clone.exceptions.EntityNotFoundException;
import udemy.clone.mapper.InvitationMapper;
import udemy.clone.model.Course;
import udemy.clone.model.Invitation;
import udemy.clone.model.User;
import udemy.clone.model.enums.InvitationStatus;
import udemy.clone.model.event.InvitationEvent;
import udemy.clone.model.invitations.InvitationDto;
import udemy.clone.producer.InvitationEventPublisher;
import udemy.clone.repository.InvitationRepository;
import udemy.clone.validators.InvitationValidator;

import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;
    private final InvitationValidator invitationValidator;
    private final UserService userService;
    private final CourseService courseService;
    private final InvitationEventPublisher invitationEventPublisher;

    public Invitation getInvitationById(String id) {
        return invitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invitation with id " + id + " not found"));
    }

    //@PreAuthorize("hasRole('TEACHER')")
    @Transactional
    public InvitationDto sendInvitation(InvitationDto invitationDto) {
        Course course = courseService.findCourseById(invitationDto.getCourseId());
        User sender = userService.findUserById(invitationDto.getSenderId());
        User recipient = userService.findUserById(invitationDto.getRecipientId());
        Invitation entity = invitationMapper.toEntity(invitationDto);
        invitationValidator.validateForSendInvitation(entity, course, sender, recipient);
        entity.setCourse(course);
        entity.setSender(sender);
        entity.setStatus(InvitationStatus.PENDING);
        entity.setRecipient(recipient);
        entity.setCreatedAt(now());
        Invitation saved = invitationRepository.save(entity);
        sendEvent(course, recipient, sender);
        return invitationMapper.toDto(saved);
    }

    @Transactional
    public InvitationDto acceptInvitation(String invitationId) {
        Invitation invitation = getInvitationById(invitationId);
        User recipient = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        invitationValidator.validateForAcceptOrRejectInvitation(invitation, recipient);

        invitation.setStatus(InvitationStatus.ACCEPTED);
        Invitation saved = invitationRepository.save(invitation);
        return invitationMapper.toDto(saved);
    }

    @Transactional
    public InvitationDto rejectInvitation(String invitationId) {
        Invitation invitation = getInvitationById(invitationId);
        User recipient = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        invitationValidator.validateForAcceptOrRejectInvitation(invitation, recipient);

        invitation.setStatus(InvitationStatus.REJECTED);
        Invitation saved = invitationRepository.save(invitation);
        return invitationMapper.toDto(saved);
    }

    @Transactional
    public void cancelInvitation(String invitationId) {
        Invitation invitation = getInvitationById(invitationId);
        User sender = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        invitationValidator.validateForCancelInvitation(invitation, sender);

        invitationRepository.delete(invitation);
    }

    private void sendEvent(Course course, User recipient, User sender) {
        InvitationEvent event = new InvitationEvent(course.getTitle(), recipient.getName(), sender.getName());
        invitationEventPublisher.publish(event);
    }
}