package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;
import com.techyourchance.unittesting.testdata.QuestionsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FetchLastActiveQuestionsUseCaseTest {

    // region constants ----------------------------------------------------------------------------
    private static final List<Question> QUESTIONS = QuestionsTestData.getQuestions();
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    /*this is a test double, I use it to simulate the behavior of the FetchLastActiveQuestionEndPoint,the whole
    class can be found at the end*/
    private EndpointTd mEndpointTd;
    //I use Mokito to create two mock objects of listener to get the notification from FetchLastActiveQuestionUseCase
    @Mock FetchLastActiveQuestionsUseCase.Listener mListener1;
    @Mock FetchLastActiveQuestionsUseCase.Listener mListener2;

    //use this one to capture the argument
    @Captor ArgumentCaptor<List<Question>> mQuestionsCaptor;
    // endregion helper fields ---------------------------------------------------------------------

    FetchLastActiveQuestionsUseCase SUT;

    @Before
    public void setup() throws Exception {
        mEndpointTd = new EndpointTd();
        SUT = new FetchLastActiveQuestionsUseCase(mEndpointTd);
    }

    /*test if the method fetchLastActiveQuestionAndNotify is called and the questions are fetched successfully,
     the listeners will be notified with the correct questions*/
    @Test
    public void fetchLastActiveQuestionsAndNotify_success_listenersNotifiedWithCorrectData() throws Exception {
        // Arrange
        //if questions are fetched successfully
        success();
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        // Act, called method fetchLastActiveQuestionAndNotify()
        SUT.fetchLastActiveQuestionsAndNotify();
        // Assert
        //check if the listeners get notified with the corrects questions.
        verify(mListener1).onLastActiveQuestionsFetched(mQuestionsCaptor.capture());
        verify(mListener2).onLastActiveQuestionsFetched(mQuestionsCaptor.capture());
        List<List<Question>> questionLists = mQuestionsCaptor.getAllValues();
        //check if the questions fetched from EndPoint are correct
        assertThat(questionLists.get(0), is(QUESTIONS));
        assertThat(questionLists.get(1), is(QUESTIONS));
    }

    /*test if the method fetchLastActiveQuestionAndNotify is called and the questions are not fetched,
 the listeners will be notified with failure*/
    @Test
    public void fetchLastActiveQuestionsAndNotify_failure_listenersNotifiedOfFailure() throws Exception {
        // Arrange
        //if questions are not fetched
        failure();
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        // Act, called method fetchLastActiveQuestionAndNotify()
        SUT.fetchLastActiveQuestionsAndNotify();
        // Assert
        //check if the listeners get notified of failure
        verify(mListener1).onLastActiveQuestionsFetchFailed();
        verify(mListener2).onLastActiveQuestionsFetchFailed();
    }

    // region helper methods -----------------------------------------------------------------------

    private void success() {
        // currently no-op
    }

    private void failure() {
        mEndpointTd.mFailure = true;
    }

    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    /*I define the behavior of the test double, if mFailure is true, which means questions are not fetched,
     the listeners are notified of failure; if mFailure is false, a list of questions will be passed to listeners*/
    private static class EndpointTd extends FetchLastActiveQuestionsEndpoint {

        public boolean mFailure;

        public EndpointTd() {
            super(null);
        }

        @Override
        public void fetchLastActiveQuestions(Listener listener) {
            if (mFailure) {
                listener.onQuestionsFetchFailed();
            } else {
                List<QuestionSchema> questionSchemas = new LinkedList<>();
                questionSchemas.add(new QuestionSchema("title1", "id1", "body1"));
                questionSchemas.add(new QuestionSchema("title2", "id2", "body2"));
                listener.onQuestionsFetched(questionSchemas);
            }
        }
    }
    // endregion helper classes --------------------------------------------------------------------

}