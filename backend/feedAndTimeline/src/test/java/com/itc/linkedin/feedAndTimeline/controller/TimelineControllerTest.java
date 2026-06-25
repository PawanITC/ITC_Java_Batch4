package com.itc.linkedin.feedAndTimeline.controller;

import com.itc.linkedin.feedAndTimeline.dto.response.TimelinePostResponse;
import com.itc.linkedin.feedAndTimeline.security.CurrentUserService;
import com.itc.linkedin.feedAndTimeline.service.TimelineService;
import com.itc.linkedin.feedAndTimeline.service.TimelineSortMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimelineControllerTest {

    @Mock
    private TimelineService timelineService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TimelineController timelineController;

    @Test
    void shouldReturnOwnTimelineUsingJwtUserId() {
        List<TimelinePostResponse> expected = List.of(response());
        when(currentUserService.getUserId(authentication)).thenReturn("user.demo");
        when(timelineService.getTimeline("user.demo", TimelineSortMode.TOP)).thenReturn(expected);

        List<TimelinePostResponse> actual = timelineController.getMyTimeline(authentication, "top");

        assertThat(actual).isEqualTo(expected);
        verify(timelineService).getTimeline("user.demo", TimelineSortMode.TOP);
    }

    @Test
    void shouldReturnTimelineByPathVariableUserId() {
        List<TimelinePostResponse> expected = List.of(response());
        when(timelineService.getTimeline("user-2", TimelineSortMode.RECENT)).thenReturn(expected);

        List<TimelinePostResponse> actual = timelineController.getTimelineByUserId("user-2", "recent");

        assertThat(actual).isEqualTo(expected);
        verify(timelineService).getTimeline("user-2", TimelineSortMode.RECENT);
    }

    private TimelinePostResponse response() {
        return TimelinePostResponse.builder()
                .postId(1L)
                .authorId("user.demo")
                .authorName("User Demo")
                .authorHeadline("Headline")
                .content("content")
                .likesCount(1)
                .commentsCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
