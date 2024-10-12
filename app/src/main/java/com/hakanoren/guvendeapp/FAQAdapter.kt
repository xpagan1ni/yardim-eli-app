import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hakanoren.guvendeapp.FAQItem
import com.hakanoren.guvendeapp.R

class FAQAdapter(private val faqList: List<FAQItem>) :
    RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    inner class FAQViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionTextView: TextView = view.findViewById(R.id.faqQuestionTextView)
        val answerTextView: TextView = view.findViewById(R.id.faqAnswerTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faq, parent, false)
        return FAQViewHolder(view)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faqItem = faqList[position]
        holder.questionTextView.text = faqItem.question
        holder.answerTextView.text = faqItem.answer

        // Başlangıçta sadece soruyu göster ve cevabı gizle
        holder.answerTextView.visibility = View.GONE

        // Soruyu tıklayınca cevabı aç/kapa yap
        holder.questionTextView.setOnClickListener {
            if (holder.answerTextView.visibility == View.GONE) {
                holder.answerTextView.visibility = View.VISIBLE
            } else {
                holder.answerTextView.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return faqList.size
    }
}