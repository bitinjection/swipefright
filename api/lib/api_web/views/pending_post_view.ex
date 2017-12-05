defmodule ApiWeb.PendingPostView do
  use ApiWeb, :view
  alias ApiWeb.PendingPostView

  def render("index.json", %{pending: pending}) do
    %{data: render_many(pending, PendingPostView, "pending_post.json")}
  end

  def render("show.json", %{pending_post: pending_post}) do
    %{data: render_one(pending_post, PendingPostView, "pending_post.json")}
  end

  def render("pending_post.json", %{pending_post: pending_post}) do
    %{id: pending_post.id,
      title: pending_post.title,
      caption: pending_post.caption,
      image: pending_post.image}
  end
end
