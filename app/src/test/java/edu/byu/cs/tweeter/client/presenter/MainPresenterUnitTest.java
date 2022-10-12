package edu.byu.cs.tweeter.client.presenter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenterUnitTest {
    private MainPresenter.MainView mockView;
    private Cache mockCache;
    private StatusService mockStatusService;

    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup()
    {
        mockView = Mockito.mock(MainPresenter.MainView.class);
        mockCache = Mockito.mock(Cache.class);
        mockStatusService = Mockito.mock(StatusService.class);



        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);

        mockCache.getInstance().setAuthToken(new AuthToken());

    }

    @Test
    public void testPostStatus_postSuccessful(){
        runDoAnswerWhen(new successAnswer());
        postStatus();
        Mockito.verify(mockView).showPostingToast(false);
        verifyMessage("Successfully Posted!");
    }

    @Test
    public void testPostStatus_postFailed(){
        runDoAnswerWhen(new failureAnswer());
        postStatus();
        verifyMessage("Failed to get post status: something bad happened");
    }

    @Test
    public void testPostStatus_postFailedWithException(){
        runDoAnswerWhen(new exceptionAnswer());
        postStatus();
        verifyMessage("Failed to get post status because of exception: my exception");
    }

    private void postStatus(){
        mainPresenterSpy.statusPost("Test string");
    }

    private void verifyMessage(String message) {
        Mockito.verify(mockView).displayMessage(message);
    }

    private void runDoAnswerWhen(answerLogic thing){
        Mockito.doAnswer(thing).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.anyString(),Mockito.anyString(),Mockito.anyList(),Mockito.anyList(),Mockito.any(SimpleNotificationObserver.class));
    }

    private abstract static class answerLogic implements Answer<Void> {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            SimpleNotificationObserver observer = invocation.getArgument(6, SimpleNotificationObserver.class);


            assertTrue(invocation.getArgument(0) instanceof AuthToken);
            assertTrue(invocation.getArgument(1) instanceof User);
            assertTrue(invocation.getArgument(2) instanceof String);
            assertTrue(invocation.getArgument(3) instanceof String);

            List<String> parseURLs = invocation.getArgument(4, List.class);
            assertTrue(parseURLs.size() == 0);

            List<String> parseMentions = invocation.getArgument(5, List.class);
            assertTrue(parseURLs.size() == 0);

            handleOperation(observer);
            return null;
        }
        protected abstract void handleOperation(SimpleNotificationObserver observer);
    }

    private static class successAnswer extends answerLogic {
        @Override
        protected void handleOperation(SimpleNotificationObserver observer) {
            observer.handleSuccess();
        }
    }

    private static class failureAnswer extends answerLogic {
        @Override
        protected void handleOperation(SimpleNotificationObserver observer) {
            observer.handleFailure("something bad happened");
        }
    }

    private static class exceptionAnswer extends answerLogic {
        @Override
        protected void handleOperation(SimpleNotificationObserver observer) {
            observer.handleException(new Exception("my exception"));
        }
    }
}
