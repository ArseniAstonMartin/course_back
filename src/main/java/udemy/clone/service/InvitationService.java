package udemy.clone.service;

import lombok.RequiredArgsConstructor;
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
import udemy.clone.repository.CourseRepository;
import udemy.clone.repository.InvitationRepository;
import udemy.clone.repository.UserRepository;
import udemy.clone.validators.InvitationValidator;

import java.util.*;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;
    private final InvitationValidator invitationValidator;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final InvitationEventPublisher invitationEventPublisher;

    public Invitation getInvitationById(String id) {
        return invitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invitation with id " + id + " not found"));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @Transactional
    public InvitationDto sendInvitation(InvitationDto invitationDto) {
        Course course = courseService.findCourseById(invitationDto.getCourseId());
        User sender = userService.findUserById(invitationDto.getSenderId());
        User recipient = userService.findUserById(invitationDto.getRecipientId());
        Invitation entity = invitationMapper.toEntity(invitationDto);
        invitationValidator.validateForSendInvitation(course, sender, recipient);
        entity.setCourseId(course.getId());
        entity.setSenderId(sender.getId());
        entity.setStatus(InvitationStatus.PENDING);
        entity.setRecipientId(recipient.getId());
        entity.setCreatedAt(now());
        Invitation saved = invitationRepository.save(entity);
        addIdsForUser(sender, saved);
        addIdsForUser(recipient, saved);
        userRepository.save(recipient);
        userRepository.save(sender);
        sendEvent(course, recipient, sender);
        return invitationMapper.toDto(saved);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public InvitationDto acceptInvitation(String invitationId) {
        Invitation invitation = getInvitationById(invitationId);
        User recipient = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        invitationValidator.validateForAcceptOrRejectInvitation(invitation, recipient);

        invitation.setStatus(InvitationStatus.ACCEPTED);
        Invitation saved = invitationRepository.save(invitation);
        Course course = courseService.findCourseById(invitation.getCourseId());
        addAcceptedIdsInvitation(course, recipient);
        courseRepository.save(course);
        userRepository.save(recipient);
        return invitationMapper.toDto(saved);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public InvitationDto rejectInvitation(String invitationId) {
        Invitation invitation = getInvitationById(invitationId);
        User recipient = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        invitationValidator.validateForAcceptOrRejectInvitation(invitation, recipient);

        invitation.setStatus(InvitationStatus.REJECTED);
        Invitation saved = invitationRepository.save(invitation);
        return invitationMapper.toDto(saved);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @Transactional
    public void cancelInvitation(String invitationId) {
        Invitation invitation = getInvitationById(invitationId);
        User sender = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User recipient = userService.findUserById(invitation.getRecipientId());
        invitationValidator.validateForCancelInvitation(invitation);
        deleteIdsForUser(sender, invitationId);
        deleteIdsForUser(recipient, invitationId);
        invitationRepository.delete(invitation);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public List<InvitationDto> getMyInvitations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findUserById(user.getId()).getInvitationsIds().stream()
                .map(this::getInvitationById)
                .map(invitationMapper::toDto)
                .toList();
    }

    private void deleteIdsForUser(User user, String invitationId) {
        List<String> invitationsIds = user.getInvitationsIds() != null ? user.getInvitationsIds() : new ArrayList<>();
        Collections.sort(invitationsIds);
        int index = Collections.binarySearch(invitationsIds, invitationId);
        invitationsIds.remove(index);
        user.setInvitationsIds(invitationsIds);
        userRepository.save(user);
    }

    private void sendEvent(Course course, User recipient, User sender) {
        InvitationEvent event = new InvitationEvent(course.getId(),
                course.getTitle(), recipient.getId(), recipient.getName(), sender.getId(), sender.getName());
        invitationEventPublisher.publish(event);
    }

    private void addAcceptedIdsInvitation(Course course, User user) {
        Set<String> studentsIds = course.getStudentsIds() != null ? course.getStudentsIds() : new HashSet<>();
        studentsIds.add(user.getId());
        course.setStudentsIds(studentsIds);
        List<String> courseIds = user.getCourseIds() != null ? user.getCourseIds() : new ArrayList<>();
        courseIds.add(course.getId());
        user.setCourseIds(courseIds);
    }

    private void addIdsForUser(User user, Invitation invitation) {
        List<String> invitationsIds =
                user.getInvitationsIds() != null ? user.getInvitationsIds() : new ArrayList<>();
        invitationsIds.add(invitation.getId());
        user.setInvitationsIds(invitationsIds);
    }
}