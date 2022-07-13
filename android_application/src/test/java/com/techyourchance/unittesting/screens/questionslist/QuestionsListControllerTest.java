package com.techyourchance.unittesting.screens.questionslist;

import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.questions.FetchLastActiveQuestionsUseCase;
import com.techyourchance.unittesting.questions.Question;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;
import com.techyourchance.unittesting.testdata.QuestionsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionsListControllerTest {

    // region constants ----------------------------------------------------------------------------
    private static final List<Question> QUESTIONS = QuestionsTestData.getQuestions();
    private static final Question QUESTION = QuestionsTestData.getQuestion();
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    /*this is a test double, I use it to simulate the behavior of the FetchLastActiveQuestionUseCase,the whole
    class can be found at the end*/
    private UseCaseTd mUseCaseTd;
    //I used Mokito to create mock objects for ScreensNavigator, ToastersHelper and QuestionsListViewMvc
    @Mock ScreensNavigator mScreensNavigator;
    @Mock ToastsHelper mToastsHelper;
    @Mock QuestionsListViewMvc mQuestionsListViewMvc;
    // endregion helper fields ---------------------------------------------------------------------

    QuestionsListController SUT;

    @Before
    public void setup() throws Exception {
        mUseCaseTd = new UseCaseTd();
        SUT = new QuestionsListController(mUseCaseTd, mScreensNavigator, mToastsHelper);
        //bind controller with the view
        SUT.bindView(mQuestionsListViewMvc);
    }

    //test if the application is turned on, the progress indication would be shown
    @Test
    public void onStart_progressIndicationShown() throws Exception {
        // Arrange
        // Act
        //call onStart() means the application is invoked
        SUT.onStart();
        // Assert
        //check if the method showProgressIndication() was called, if the application has been invoked
        verify(mQuestionsListViewMvc).showProgressIndication();
    }

    //test if the application is turned on for a while, the progress indication would be hidden
    @Test
    public void onStart_successfulResponse_progressIndicationHidden() throws Exception {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        //check if the method hideProgressIndication() was called, if the application has been invoked
        verify(mQuestionsListViewMvc).hideProgressIndication();
    }

    //test if the application is turned on, but the questions can not be fetched from EndPoint
    // the progress indication would be hidden
    @Test
    public void onStart_failure_progressIndicationHidden() throws Exception {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        //check if the method hideProgressIndication() was called, if the application has been invoked
        verify(mQuestionsListViewMvc).hideProgressIndication();
    }

    //test if the application is turned on, and the questions are fetched successfully from EndPoint
    //the questions will be displayed in the view.
    @Test
    public void onStart_successfulResponse_questionsBoundToView() throws Exception {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        //check if the method bindQuestions() was called, if the questions are fetched successfully from EndPoint
        verify(mQuestionsListViewMvc).bindQuestions(QUESTIONS);
    }

    //Test if the questions are stored in the cache, questions will be displayed in the view directly, no need to
    //call the method fetchLastActiveQuestionsAndNotify()
    @Test
    public void onStart_secondTimeAfterSuccessfulResponse_questionsBoundToTheViewFromCache() throws Exception {
        // Arrange
        success();
        // Act
        SUT.onStart();
        //call onStart() twice, which means that the questions are still in the cache after calling onStart() first time
        SUT.onStart();
        // Assert
        //check if the method bindQuestion() was called exactly twice
        verify(mQuestionsListViewMvc, times(2)).bindQuestions(QUESTIONS);
        //check if it only call the method fetchLastActiveQuestionsAndNotify() once
        assertThat(mUseCaseTd.getCallCount(), is(1));
    }

    //test when questions are not fetched, the error toast will be displayed in the view
    @Test
    public void onStart_failure_errorToastShown() throws Exception {
        // Arrange
        //set condition to be failure.
        failure();
        // Act
        SUT.onStart();
        // Assert
        //check if the method showUseCassError will be called.
        verify(mToastsHelper).showUseCaseError();
    }

    //test when questions are not fetched, no questions will be displayed in the view
    @Test
    public void onStart_failure_questionsNotBoundToView() throws Exception {
        // Arrange
        //set condition to be failure
        failure();
        // Act
        SUT.onStart();
        // Assert
        //check if the method bindQuestions will never be called
        verify(mQuestionsListViewMvc, never()).bindQuestions(any(List.class));
    }

    //test if the controller is registered as a listener of the view and the fetchLastActiveQuestionsUseCase when
    // the application is invoked
    @Test
    public void onStart_listenersRegistered() throws Exception {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        //check if the method registerListener was called
        verify(mQuestionsListViewMvc).registerListener(SUT);
        //check if the controller is the one of the listener of mUseCaseTd
        mUseCaseTd.verifyListenerRegistered(SUT);
    }

    //test if the controller is unregistered when the application is stopped
    @Test
    public void onStop_listenersUnregistered() throws Exception {
        // Arrange
        SUT.onStart();
        // Act
        SUT.onStop();
        // Assert
        //check if the method unregisterListener was called
        verify(mQuestionsListViewMvc).unregisterListener(SUT);
        //check if the controller is not longer the one of the listener of mUseCaseTd
        mUseCaseTd.verifyListenerNotRegistered(SUT);
    }

    //test when user click one of the questions, the user will be navigated to the detail of this question
    @Test
    public void onQuestionClicked_navigatedToQuestionDetailsScreen() throws Exception {
        // Arrange
        // Act
        SUT.onQuestionClicked(QUESTION);
        // Assert
        //check if the method toQuestionDetails() was called
        verify(mScreensNavigator).toQuestionDetails(QUESTION.getId());
    }



    // region helper methods -----------------------------------------------------------------------

    private void success() {
        // currently no-op
    }

    private void failure() {
        mUseCaseTd.mFailure = true;
    }

    private void emptyQuestionsListOnFirstCall() {
        mUseCaseTd.mEmptyListOnFirstCall = true;
    }

    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    private static class UseCaseTd extends FetchLastActiveQuestionsUseCase {

        public boolean mEmptyListOnFirstCall;
        private boolean mFailure;
        private int mCallCount;

        public UseCaseTd() {
            super(null);
        }

        @Override
        public void fetchLastActiveQuestionsAndNotify() {
            mCallCount++;
            for (FetchLastActiveQuestionsUseCase.Listener listener : getListeners()) {
                if (mFailure) {
                    listener.onLastActiveQuestionsFetchFailed();
                } else {
                    if (mEmptyListOnFirstCall && mCallCount == 1) {
                        listener.onLastActiveQuestionsFetched(new LinkedList<Question>());
                    } else {
                        listener.onLastActiveQuestionsFetched(QUESTIONS);
                    }
                }
            }
        }

        public void verifyListenerRegistered(QuestionsListController candidate) {
            for (FetchLastActiveQuestionsUseCase.Listener listener : getListeners()) {
                if (listener == candidate) {
                    return;
                }
            }
            throw new RuntimeException("listener not registered");
        }

        public void verifyListenerNotRegistered(QuestionsListController candidate) {
            for (FetchLastActiveQuestionsUseCase.Listener listener : getListeners()) {
                if (listener == candidate) {
                    throw new RuntimeException("listener not registered");
                }
            }
        }

        public int getCallCount() {
            return mCallCount;
        }
    }
    // endregion helper classes --------------------------------------------------------------------

}