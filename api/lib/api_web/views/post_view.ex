defmodule ApiWeb.PostView do
  use ApiWeb, :view
  alias ApiWeb.PostView

  def render("index.json", %{posts: posts}) do
    %{data: render_many(posts, PostView, "post.json")}
  end

  def render("show.json", %{post: post}) do
    %{data: render_one(post, PostView, "post.json")}
  end

  def render("show.json", %{total: total}) do
    %{data: render_one(total, PostView, "count.json", as: :total)}
  end

  def render("show.json", %{id: id}) do
    %{data: render_one(id, PostView, "id.json", as: :id)}
  end

  def render("id.json", %{id: id}) do
    %{id: id}
  end

  def render("count.json", %{total: total}) do
    %{total: total}
  end

  def render("post.json", %{post: post}) do
    %{id: post.id,
      title: post.title,
      caption: post.caption,
      images: render_many(post.images, ApiWeb.ImageView, "image.json")}
  end
end
