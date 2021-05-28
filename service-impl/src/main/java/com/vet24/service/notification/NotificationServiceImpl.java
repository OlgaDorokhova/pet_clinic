package com.vet24.service.notification;

import com.vet24.dao.ReadWriteDaoImpl;
import com.vet24.dao.notification.NotificationDao;
import com.vet24.models.dto.googleEvent.GoogleEventDto;
import com.vet24.models.exception.BadRequestException;
import com.vet24.models.mappers.notification.NotificationEventMapper;
import com.vet24.models.notification.Notification;
import com.vet24.service.ReadWriteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class NotificationServiceImpl extends ReadWriteServiceImpl<Long, Notification> implements NotificationService {

    private final NotificationDao notificationDao;
    private final NotificationEventMapper notificationEventMapper;
    private final GoogleEventService googleEventService;

    @Autowired
    public NotificationServiceImpl(ReadWriteDaoImpl<Long, Notification> readWriteDao, NotificationDao notificationDao,
                                   NotificationEventMapper notificationEventMapper, GoogleEventService googleEventService) {
        super(readWriteDao);
        this.notificationDao = notificationDao;
        this.notificationEventMapper = notificationEventMapper;
        this.googleEventService = googleEventService;
    }

    @Override
    public void persist(Notification notification) {
        GoogleEventDto googleEventDto = notificationEventMapper
                .notificationToGoogleEventDto(notification, notification.getPet().getClient().getEmail());

        try {
            googleEventService.createEvent(googleEventDto);
        } catch (IOException exception) {
            throw new BadRequestException(exception.getMessage(), exception.getCause());
        }
        notification.setEvent_id(googleEventDto.getId());

        super.persist(notification);
    }

    @Override
    public Notification update(Notification notification) {
        GoogleEventDto googleEventDto = notificationEventMapper
                .notificationToGoogleEventDto(notification, notification.getPet().getClient().getEmail());

        try {
            googleEventService.editEvent(googleEventDto);
        } catch (IOException exception) {
            throw new BadRequestException(exception.getMessage(), exception.getCause());
        }

        return super.update(notification);
    }

    @Override
    public void delete(Notification notification) {
        GoogleEventDto googleEventDto = new GoogleEventDto();
        googleEventDto.setId(notification.getEvent_id());
        googleEventDto.setEmail(notification.getPet().getClient().getEmail());

        try {
            googleEventService.deleteEvent(googleEventDto);
        } catch (IOException exception) {
            throw new BadRequestException(exception.getMessage(), exception.getCause());
        }

        super.delete(notification);
    }
}
